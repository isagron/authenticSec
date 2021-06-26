package com.isagron.security.services.validators;

import com.isagron.security.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidationService {

    @Autowired
    private UserRepository userRepository;


    public ValidationResultHandling validateUserNotExist(String userName) {
        return userRepository.existsByUserName(userName) ?
                ValidationResultHandling.invalid() :
                ValidationResultHandling.valid();
    }

    public ValidationResultHandling validateMailNotExist(String email) {
        return userRepository.existsByEmail(email) ?
                ValidationResultHandling.invalid() :
                ValidationResultHandling.valid();
    }
}
