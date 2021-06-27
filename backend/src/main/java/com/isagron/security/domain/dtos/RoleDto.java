package com.isagron.security.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
public class RoleDto {

    @NotBlank(message = "You must provide role name")
    private String name;

    @NotEmpty(message = "Role must have authorities associate to it")
    private List<String> authorities;
}
