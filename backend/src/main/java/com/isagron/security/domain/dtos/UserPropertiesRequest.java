package com.isagron.security.domain.dtos;

import lombok.Data;

/**
 * Use for create and update user
 */
@Data
public class UserPropertiesRequest {

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String role;

    private Boolean isLock;

    private Boolean isActive;

    private Boolean verifyByEmail;

    public boolean isActive() {
        return isActive!= null? isActive : false;
    }

    public boolean isLock() {
        return isLock!= null? isLock : false;
    }

    public boolean isVerifyByEmail() {
        return verifyByEmail!= null? verifyByEmail : false;
    }
}
