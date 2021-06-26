package com.isagron.security.domain.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UpdateUserRequest {

    @NotBlank
    private String userName;

    private UserPropertiesRequest userPropertiesRequest;
}
