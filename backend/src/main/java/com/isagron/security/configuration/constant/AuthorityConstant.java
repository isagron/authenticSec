package com.isagron.security.configuration.constant;

public class AuthorityConstant {

    public static final String[] USER_AUTHORITIES = {"user:read"};
    public static final String[] ADMIN_AUTHORITIES = {"user:read", "user:create", "user:update"};
    public static final String[] SUPER_USER_AUTHORITIES = {"user:read", "user:create", "user:update", "user:delete"};
}
