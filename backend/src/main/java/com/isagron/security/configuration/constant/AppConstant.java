package com.isagron.security.configuration.constant;

public class AppConstant {

    public static final String AUTHORITIES = "Authorities";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/",
            "/auth/login",
            "/auth/register",
            "/auth/confirm",
            "/auth/request-reset-password",
            "/auth/reset-password",
            "/auth/is-valid-code-for-reset",
            "/user/image/**",
            //swagger
            "/swagger-ui.html/**", "/configuration/**", "/swagger-resources/**", "/v2/api-docs","/webjars/**"};

    public final static String DOT = ".";

    public static final String JPG_EXTENSION = "jpg";

    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";

}
