package com.isagron.security.domain.dtos;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;

@Data
@FieldNameConstants
public class LoginRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

}
