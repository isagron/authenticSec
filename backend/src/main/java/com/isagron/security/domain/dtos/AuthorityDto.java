package com.isagron.security.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO class, represent Authority element
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDto {

    /**
     * Authority identifier
     */
    private long id;

    /**
     * The name of the authority
     */
    @NotBlank(message = "You must provide the name of the authority")
    private String name;

    /**
     * Role names, which this authority appear
     */
    private List<String> roles;
}
