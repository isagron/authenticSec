package com.isagron.security.services.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UpdateRequestValidator implements OperationValidator {


    @Override
    public AppOperation getOperation() {
        return AppOperation.UPDATE_USER;
    }


    @Override
    public void validate(Object subject, Errors e) {
        //validate old user name exist

        //validate new user name not exist

        //validate old user name different from new user name
    }
}
