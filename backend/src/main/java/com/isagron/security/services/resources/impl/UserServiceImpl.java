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
import com.isagron.security.utils.AppConstant;
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

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


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

    private <T> void updateProp(Supplier<T> getter, Consumer<? super T> consumer) {
        if (getter.get() != null) {
            consumer.accept(getter.get());
        }
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> new UserNotFoundException("User not exist"));
        userRepository.deleteByUserId(id);
        this.notificationService.sendDeleteMessage(convertToDto(user));
    }

    @Override
    @Transactional
    public void lockUser(String userName) {
        User user = findUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        user.setLocked(true);
    }

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

    private String getDefaultImageUrl(String userName) {
        return AppConstant.TEMP_PROFILE_IMAGE_BASE_URL + userName;
    }

    @Override
    public User updateProfileImage(String userName, MultipartFile profileImage) {
        User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    @Transactional
    public User addNewUser(UserPropertiesRequest userProperties) {

        String password = passwordEncoder.encode(generatePassword());

        User user = createUser(userProperties.getFirstName(), userProperties.getLastName(), userProperties.getUserName(),
                userProperties.getEmail(), password, userProperties.isActive(), userProperties.isLock(), userProperties.isVerifyByEmail(), userProperties.getRole(),
                null);

        return user;
    }

    @Transactional
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

    private String generatePassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public User getOrElseThrow(String userName) {
        return userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));
    }

    @Override
    public User getByEmailOrElseThrow(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
    }

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

    private UserDto convertToDto(User user){
        return this.modelMapper.map(user, UserDto.class);
    }


}
