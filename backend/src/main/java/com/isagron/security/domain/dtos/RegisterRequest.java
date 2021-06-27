package com.isagron.security.domain.dtos;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;

/**
 * DTO class user for register API
 */
@Data
@FieldNameConstants
public class RegisterRequest {

    @NotBlank(message = "You must provide user name")
    private String userName;

    @NotBlank(message = "You must provide first name to register")
    private String firstName;

    @NotBlank(message = "You must provide last name to register")
    private String lastName;

    @NotBlank(message = "You must provide email to register")
    private String email;

    @NotBlank(message = "You must provide password to register")
    private String password;

    @NotBlank(message = "You must confirm you password to register")
    private String confirmPassword;

    private String profileImageUrl;

}
