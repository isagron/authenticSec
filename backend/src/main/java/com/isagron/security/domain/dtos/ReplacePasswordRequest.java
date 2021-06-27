package com.isagron.security.domain.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReplacePasswordRequest {

    @NotBlank(message = "You must provide user name to replace password")
    private String userName;

    @NotBlank(message = "You must provide new password")
    private String newPassword;

    @NotBlank(message = "You must confirm your password")
    private String confirmPassword;
    
    private String confirmation;
}
