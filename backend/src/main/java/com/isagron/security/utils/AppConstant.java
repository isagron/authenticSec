package com.isagron.security.utils;

public class AppConstant {

    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can't be verified";
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
            "/user/image/**"};

    public final static String DOT = ".";

    public final static String FILE_DELIMITER = "/";

    public final static String USER_IMAGE_PATH = "/image";

    public static final String JPG_EXTENSION = "jpg";

    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";

}
