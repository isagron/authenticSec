package com.isagron.security.domain.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "You must provide user name, to identified the user you want to update")
    private String userName;

    private UserPropertiesRequest userPropertiesRequest;
}
