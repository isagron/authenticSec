package com.isagron.security.services.resources.impl;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import com.isagron.security.domain.dtos.UpdateUserRequest;
import com.isagron.security.domain.dtos.UserDto;
import com.isagron.security.domain.dtos.UserPropertiesRequest;
import com.isagron.security.domain.entities.Role;
import com.isagron.security.domain.entities.User;
import com.isagron.security.domain.model.UserPrincipal;
import com.isagron.security.domain.repositories.UserRepository;
import com.isagron.security.exceptions.AppErrorCode;
import com.isagron.security.exceptions.EmailNotFoundException;
import com.isagron.security.exceptions.RoleNotExistException;
import com.isagron.security.exceptions.SaveProfileImageException;
import com.isagron.security.exceptions.SecurityAppException;
import com.isagron.security.exceptions.UserNotFoundException;
import com.isagron.security.services.client_notification.WebSocketService;
import com.isagron.security.services.resources.RoleService;
import com.isagron.security.services.resources.UserService;
import com.isagron.security.services.validators.AppOperation;
import com.isagron.security.services.validators.ValidatorMng;
import com.isagron.security.configuration.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorMng validator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Value("${app.user.image.folder}")
    private String userImageFolder;

    @Autowired
    private WebSocketService notificationService;

    /**
     * Inherits method from UserDetailsService
     * Load user by user name, update login dates information
     * @param username - username of the login user
     * @return - UserDetails object
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUserName(username)
                .map(user -> {
                    log.info("Find user by userName: " + username);
                    user.setLastLoginDateDisplay(user.getLastLoginDate());
                    user.setLastLoginDate(new Date());
                    return new UserPrincipal(user);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User with name: " + username + " not found"));
    }

    /**
     * This method return all the users exists
     * @return list of Users
     */
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * This method return User by username
     * @param userName - the username of the user
     * @return - Optional User, empty if not found
     */
    @Override
    public Optional<User> findUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName);
    }

    /**
     * This method return User by email
     * @param email - the email of the user
     * @return - Optional User, empty if not found
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    /**
     * Update user by UpdateUserRequest.userName
     * Set only the field that not null in updateUserRequest @Param
     * @param updateUserRequest - including the original userName, and container of user attribute to update
     * @return - return the user after update
     */
    @Override
    @Transactional
    public User updateUser(UpdateUserRequest updateUserRequest) {
        validator.validate(AppOperation.UPDATE_USER, updateUserRequest);
        User user = userRepository.findUserByUserName(updateUserRequest.getUserName())
                .orElseThrow(() -> new UserNotFoundException(updateUserRequest.getUserName()));
        UserPropertiesRequest updateProperties = updateUserRequest.getUserPropertiesRequest();
        updateProp(updateProperties::getUserName, user::setUserName);
        updateProp(updateProperties::getEmail, user::setEmail);
        updateProp(updateProperties::getFirstName, user::setFirstName);
        updateProp(updateProperties::getLastName, user::setLastName);
        updateProp(updateProperties::getIsActive, user::setActive);
        updateProp(updateProperties::getIsLock, user::setLocked);
        roleService.findRoleByName(updateProperties.getRole())
                .ifPresent(user::setRole);
        this.notificationService.sendUpdateMessage(convertToDto(user));
        return user;
    }


    /**
     * Delete user by userId
     * @param id - the userId
     */
    @Override
    @Transactional
    public void deleteUser(String id) {
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> new UserNotFoundException("User not exist"));
        userRepository.deleteByUserId(id);
        this.notificationService.sendDeleteMessage(convertToDto(user));
    }

    /**
     * Set the isLock attribute to true
     * Throw exception in case user not found, or failed to update
     * @param userName - the userName of the user to lock
     */
    @Override
    @Transactional
    public void lockUser(String userName) {
        User user = findUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        user.setLocked(true);
    }

    /**
     * Create new user in the database
     * @param firstName - user first name
     * @param lastName - user last name
     * @param userName - user user name, must be unique
     * @param email - user email, must be unique
     * @param encodePassword - password after encoding
     * @param isActive - flag indicate if user is active
     * @param isLock - flag indicate if the user is lock
     * @param emailVerification - flag indicate if user need to be verified by confirmation code via email
     * @param roleName - the name of the role set for the user, if role not found throw an exception
     * @param profileImageUrl - url, reference to the image, the UI app responsible for loading the image from the URL
     * @return - return the newly created user
     */
    @Override
    @Transactional
    public User createUser(String firstName, String lastName, String userName, String email, String encodePassword,
                           boolean isActive, boolean isLock, boolean emailVerification, String roleName, String profileImageUrl) {
        Role role = roleService.findRoleByName(roleName)
                .orElseThrow(() -> new RoleNotExistException(roleName));

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .userName(userName)
                .email(email)
                .joinDate(new Date())
                .password(encodePassword)
                .isActive(isActive)
                .isLocked(isLock)
                .emailVerification(emailVerification)
                .needToReplacePassword(false)
                .profileImageUrl(profileImageUrl != null ? profileImageUrl : getDefaultImageUrl(userName))
                .lastTimeRenewPassword(new Date())
                .successfulLogin(0)
                .failureLogin(0)
                .build();

        user.setRole(role);
        log.info("New user register: {}", userName);
        User persistUser = userRepository.save(user);
        this.notificationService.sendCreateMessage(convertToDto(persistUser));
        return persistUser;
    }


    /**
     * This method upload image file to the server repository, and update the user image url accordingly
     * The user can fetch the image from the getImage API
     * @param userName - the user name
     * @param profileImage - image file
     * @return - return the user after update
     */
    @Override
    public User updateProfileImage(String userName, MultipartFile profileImage) {
        User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        saveProfileImage(user, profileImage);
        return user;
    }

    /**
     * Create new user with the properties set in the userProperties @Param
     * @param userProperties - container of user properties
     * @return
     */
    @Override
    @Transactional
    public User addNewUser(UserPropertiesRequest userProperties) {

        String password = passwordEncoder.encode(generatePassword());

        User user = createUser(userProperties.getFirstName(), userProperties.getLastName(), userProperties.getUserName(),
                userProperties.getEmail(), password, userProperties.isActive(), userProperties.isLock(), userProperties.isVerifyByEmail(), userProperties.getRole(),
                null);

        return user;
    }


    /**
     * Return the user by userName, if user not found throw UserNotFoundException
     * @param userName - the user name
     * @return - return user
     */
    @Override
    public User getOrElseThrow(String userName) {
        return userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));
    }
    /**
     * Return the user by userName, if user not found throw EmailNotFoundException
     * @param email - the user email
     * @return - return user
     */
    @Override
    public User getByEmailOrElseThrow(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
    }


    /**
     * Return byte array of the user image
     * @param userName - user name as user identifier
     * @return - byte array of user image
     */
    @Override
    public byte[] getProfileImage(String userName) {
        User user = getOrElseThrow(userName);
        try {
            Path userImagePath = Paths.get(userImageFolder + "/" + user.getUserName() + AppConstant.DOT + AppConstant.JPG_EXTENSION);
            return Files.readAllBytes(userImagePath);
        } catch (IOException e) {
            throw new SecurityAppException(AppErrorCode.IMAGE_NOT_FOUND, AppErrorCode.IMAGE_NOT_FOUND.defaultMessageFormat());
        }
    }

    /**
     * Return Page of users (according to the pageable param)
     * The response filter following the user name and role
     * @param userName - user name
     * @param role - search filter by role name
     * @param p - pageable
     * @return - users page
     */
    @Override
    public Page<User> getUsers(String userName, String role, Pageable p) {
        if (userName == null && role == null) {
            return this.userRepository.findAll(p);
        }
        if (userName != null && role != null) {
            return this.userRepository.findAllByUserNameAndRole(userName, role, p);
        }
        if (userName != null) {
            return this.userRepository.findAllByUserName(userName, p);
        }
        return this.userRepository.findAllByRole(role, p);
    }

    @Override
    public boolean isPasswordExpire(String userName, int time) {
        User user = getOrElseThrow(userName);
        Date passwordCreated = user.getLastTimeRenewPassword();
        Calendar inst = Calendar.getInstance();
        inst.setTime(passwordCreated);
        inst.add(Calendar.HOUR, time);
        return new Date().compareTo(inst.getTime()) > 0;
    }

    /**
     * convert User entity to UserDto
     * @param user - the user to convert
     * @return - User represent via UserDto
     */
    private UserDto convertToDto(User user){
        return this.modelMapper.map(user, UserDto.class);
    }

    /**
     * Update the user property, update the value only if the source is not null
     * @param getter - Functional interface to get the source value of the property to update
     * @param consumer - Funcational interface for setting the value of the property to update
     * @param <T> - the type of the property to update
     */
    private <T> void updateProp(Supplier<T> getter, Consumer<? super T> consumer) {
        if (getter.get() != null) {
            consumer.accept(getter.get());
        }
    }

    /**
     * Generate random password base on UUID
     * @return - return new password
     */
    private String generatePassword() {
        return UUID.randomUUID().toString();
    }

    /**
     * Save image file into image repository
     * @param user - the user assoiciate with the image
     * @param profileImage - image file
     */
    private void saveProfileImage(User user, MultipartFile profileImage) {
        if (profileImage != null) {
            try {
                Path userImagePath = Paths.get(userImageFolder + "/" + user.getUserName() + AppConstant.DOT + AppConstant.JPG_EXTENSION);
                Path imageFolder = Paths.get(userImageFolder);
                Files.deleteIfExists(userImagePath);
                Files.copy(profileImage.getInputStream(), imageFolder.resolve(user.getUserName() + AppConstant.DOT + AppConstant.JPG_EXTENSION), REPLACE_EXISTING);
                user.setProfileImageUrl(ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path(userImagePath.toString())
                        .toUriString());
                userRepository.save(user);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SaveProfileImageException(user.getUserName());
            }
        }
    }

    /**
     * Return default image URL from robohash API
     * @param userName - the user name
     * @return - image url
     */
    private String getDefaultImageUrl(String userName) {
        return AppConstant.TEMP_PROFILE_IMAGE_BASE_URL + userName;
    }


}
