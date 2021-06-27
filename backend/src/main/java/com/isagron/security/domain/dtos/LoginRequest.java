package com.isagron.security.domain.dtos;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;

/**
 * DTO user for login API
 */
@Data
@FieldNameConstants
public class LoginRequest {

    @NotBlank(message = "User name can't be blank")
    private String userName;

    @NotBlank(message = "Password can't be blank")
    private String password;

}
