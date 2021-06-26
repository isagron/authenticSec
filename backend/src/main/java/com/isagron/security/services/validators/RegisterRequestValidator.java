package com.isagron.security.services.validators;

import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.exceptions.AppErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class RegisterRequestValidator implements OperationValidator {

    @Autowired
    private UserValidationService userValidationService;

    @Override
    public AppOperation getOperation() {
        return AppOperation.REGISTER_USER;
    }

    public boolean supports(Class clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    public void validate(Object req, Errors errors) {
        RegisterRequest registerRequest = (RegisterRequest) req;
        //validate user name not exist
        userValidationService.validateUserNotExist(registerRequest.getUserName())
                .onError(() -> errors.rejectValue(
                        RegisterRequest.Fields.userName,
                        AppErrorCode.USER_NAME_ALREADY_EXIST.code(),
                        String.format(AppErrorCode.USER_NAME_ALREADY_EXIST.defaultMessageFormat(), registerRequest.getEmail())
                ));

        //validate mail not exist
        userValidationService.validateMailNotExist(registerRequest.getEmail())
                .onError(() -> errors.rejectValue(
                        RegisterRequest.Fields.email,
                        AppErrorCode.MAIL_ALREADY_EXIST.code(),
                        String.format(AppErrorCode.MAIL_ALREADY_EXIST.defaultMessageFormat(), registerRequest.getEmail())
                        ));
    }

}
