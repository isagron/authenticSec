package com.isagron.security.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role;
    private List<String> authorities;
    private boolean isActive;
    private boolean isNotLocked;
    private boolean emailVerification;

    private boolean needToReplacePassword;

    private int successfulLogin;
    private int failureLogin;

    private Date lastTimeRenewPassword;
}
