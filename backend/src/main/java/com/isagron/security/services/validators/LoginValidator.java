package com.isagron.security.services.validators;

import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.entities.User;
import com.isagron.security.domain.repositories.UserRepository;
import com.isagron.security.exceptions.AppErrorCode;
import com.isagron.security.services.features.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginValidator implements OperationValidator {

    private final UserRepository userRepository;


    @Override
    public AppOperation getOperation() {
        return AppOperation.LOGIN;
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginRequest req = (LoginRequest) target;
        Optional<User> opUser = userRepository.findUserByUserName(req.getUserName());
        if (!opUser.isPresent()) {
            errors.rejectValue(LoginRequest.Fields.userName, AppErrorCode.USER_NAME_NOT_EXIST.code(),
                    String.format(AppErrorCode.USER_NAME_NOT_EXIST.defaultMessageFormat(), req.getUserName()));
        } else {
            User user = opUser.get();
            if (user.isEmailVerification()){
                errors.rejectValue(LoginRequest.Fields.userName, AppErrorCode.MISSING_VERIFICATION_CODE.code());
            }
            if (user.isNeedToReplacePassword()){
                errors.rejectValue(LoginRequest.Fields.userName, AppErrorCode.PASSWORD_RESET.code());
            }
        }

    }
}
