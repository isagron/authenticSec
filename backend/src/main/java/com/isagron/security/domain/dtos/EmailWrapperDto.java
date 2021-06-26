package com.isagron.security.domain.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmailWrapperDto {
    @NotBlank
    private String email;
}
