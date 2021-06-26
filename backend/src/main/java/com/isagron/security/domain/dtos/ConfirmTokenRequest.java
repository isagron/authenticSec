package com.isagron.security.domain.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfirmTokenRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String code;
}
