package com.isagron.security.services.resources;

import com.isagron.security.domain.dtos.UpdateUserRequest;
import com.isagron.security.domain.dtos.UserPropertiesRequest;
import com.isagron.security.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<User> getUsers();

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(String email);

    User addNewUser(UserPropertiesRequest userPropertiesRequest);

    User updateUser(UpdateUserRequest updateUserRequest);

    void deleteUser(String id);

    void lockUser(String userName);

    User createUser(String firstName, String lastName, String userName, String email, String encodePassword,
                    boolean isActive, boolean isLock, boolean emailVerification, String role, String profileImageUrl);

    User updateProfileImage(String userName, MultipartFile profileImage);

    User getOrElseThrow(String userName);
    User getByEmailOrElseThrow(String email);


    byte[] getProfileImage(String userName);

    Page<User> getUsers(String userName, String role, Pageable p);

}
