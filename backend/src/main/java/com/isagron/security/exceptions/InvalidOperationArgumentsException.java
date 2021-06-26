package com.isagron.security.exceptions;

import lombok.Data;
import org.springframework.validation.Errors;
@Data
public class InvalidOperationArgumentsException extends RuntimeException {

    private Errors errors;

    public InvalidOperationArgumentsException(Errors errors){
        super(messageFromErrors(errors));
        this.errors = errors;
    }

    public InvalidOperationArgumentsException(String message, Errors errors){
        super(message);
        this.errors = errors;
    }

    public static String messageFromErrors(Errors errors){
        if (errors != null){
            StringBuilder stringBuilder = new StringBuilder();
            errors.getAllErrors()
                    .forEach(error -> {
                        stringBuilder.append("Error code: ").append(error.getCode()).append(", ")
                                .append("message: ").append(error.getDefaultMessage());
                    });
            return stringBuilder.toString();
        }
        return "No message";
    }
}
