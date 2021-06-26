package com.isagron.security.domain.types;

import com.isagron.security.utils.AuthorityConstant;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum  DefaultRoleType {
    ROLE_USER(AuthorityConstant.USER_AUTHORITIES),
    ROLE_ADMIN(AuthorityConstant.ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(AuthorityConstant.SUPER_USER_AUTHORITIES);

    @Getter
    private List<String> authorities;

    private DefaultRoleType(String... authorities) {
        this.authorities = Arrays.asList(authorities);
    }

}
