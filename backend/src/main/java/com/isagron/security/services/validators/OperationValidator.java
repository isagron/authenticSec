package com.isagron.security.services.validators;

import org.springframework.validation.Validator;

public interface OperationValidator extends Validator {

    AppOperation getOperation();

    default boolean supports(Class clazz) {
        return getOperation().getParameterClass().equals(clazz);
    }


}
