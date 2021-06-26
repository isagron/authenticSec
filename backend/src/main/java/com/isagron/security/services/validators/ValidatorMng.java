package com.isagron.security.services.validators;

import com.isagron.security.exceptions.InvalidOperationArgumentsException;
import com.isagron.security.services.resources.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ValidatorMng {

    private Map<AppOperation, OperationValidator> validatorMap;

    @Autowired
    public ValidatorMng(List<OperationValidator> validators) {
        this.validatorMap = validators.stream()
                .collect(Collectors.toMap(OperationValidator::getOperation, validator -> validator));
    }

    public void validate(AppOperation userOperation, Object obj, String message){
        Errors errors = new BeanPropertyBindingResult(obj, obj.getClass().getName());
        ValidationUtils.invokeValidator(validatorMap.get(userOperation),
                obj, errors);
        if (errors.hasErrors()){
            throw new InvalidOperationArgumentsException(message, errors);
        }
    }
    public void validate(AppOperation userOperation, Object obj){
        Errors errors = new BeanPropertyBindingResult(obj, obj.getClass().getName());
        ValidationUtils.invokeValidator(validatorMap.get(userOperation),
                obj, errors);
        if (errors.hasErrors()){
            throw new InvalidOperationArgumentsException(errors);
        }
    }
}
