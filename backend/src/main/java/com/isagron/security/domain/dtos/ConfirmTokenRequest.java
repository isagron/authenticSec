package com.isagron.security.domain.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * DTO class, represent a request to validate confirmation code
 */
@Data
public class ConfirmTokenRequest {


    /**
     * The user username as identifier
     */
    @NotBlank(message = "You must provide a user name")
    private String userName;

    /**
     * The code to confirm
     */
    @NotBlank(message = "You must provide a code")
    private String code;
}
