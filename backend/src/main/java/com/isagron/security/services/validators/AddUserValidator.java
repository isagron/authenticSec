package com.isagron.security.services.validators;

import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.UserPropertiesRequest;
import com.isagron.security.exceptions.AppErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AddUserValidator implements OperationValidator {

    @Autowired
    private UserValidationService userValidationService;

    @Override
    public AppOperation getOperation() {
        return AppOperation.ADD_USER;
    }

    public boolean supports(Class clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    public void validate(Object req, Errors errors) {
        UserPropertiesRequest userPropertiesRequest = (UserPropertiesRequest) req;
        //validate user name not exist
        userValidationService.validateUserNotExist(userPropertiesRequest.getUserName())
                .onError(() -> errors.rejectValue(
                        RegisterRequest.Fields.userName,
                        AppErrorCode.USER_NAME_ALREADY_EXIST.code(),
                        String.format(AppErrorCode.USER_NAME_ALREADY_EXIST.defaultMessageFormat(), userPropertiesRequest.getEmail())
                ));

        //validate mail not exist
        userValidationService.validateMailNotExist(userPropertiesRequest.getEmail())
                .onError(() -> errors.rejectValue(
                        RegisterRequest.Fields.email,
                        AppErrorCode.MAIL_ALREADY_EXIST.code(),
                        String.format(AppErrorCode.MAIL_ALREADY_EXIST.defaultMessageFormat(), userPropertiesRequest.getEmail())
                ));
    }
}
