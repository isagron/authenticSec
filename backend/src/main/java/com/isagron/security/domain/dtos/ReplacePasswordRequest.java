package com.isagron.security.domain.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReplacePasswordRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
    
    private String confirmation;
}
