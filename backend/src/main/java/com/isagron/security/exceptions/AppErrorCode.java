package com.isagron.security.exceptions;

public enum  AppErrorCode {

    USER_NAME_ALREADY_EXIST("user_name_already_exist", "User name already exist: %s"),
    MAIL_ALREADY_EXIST("mail_already_exist", "Mail already exist: %s"),
    USER_NAME_NOT_EXIST("user_not_exit", "User name not exist: %s"),
    MISSING_VERIFICATION_CODE("missing_verification_code", "Please insert the code sent to your mail"),
    CONFIRMATION_TOKEN_NOT_EXIST("confirmation_token_not_exist", "Confirmation token not exist"),
    CONFIRMATION_TOKEN_INVALID("confirmation_token_invalid", "Confirmation token is invalid"),
    SAVE_IMAGE("user.save_image", "Failed to save image for user: %s" ),
    IMAGE_NOT_FOUND("user.image_not_found", "Failed to retrieve image" ),
    ROLE_NOT_EXIST("role.not_exist", "Role not exist" ),
    ROLE_ALREADY_EXIST("role.already_exist", "Role %s already exist" ),
    AUTHORITY_NOT_EXIST("role.authorities.not_exist", "Authority %s not exist" ),
    AUTHORITY_ALREADY_EXIST("role.authorities.exist", "Authority %s already exist" ),
    LOGIN_ATTEMPTS_EXCEED("login.attempts_exceed", "To many login attempts, account has been lock" ),


    PASSWORD_RESET("security.authentication.password.expire",
            "You need to reset your password"),
    VERIFICATION_FAILURE("security.authentication.token.verification",
            "Token can't be verified"),

    ACCOUNT_DISABLED_MESSAGE("security.authentication.account.disable",
            "Your account has been disabled. If this is an error, please contact administration"),
    FORBIDDEN_MESSAGE("security.authentication.forbidden",
            "You need to login to access this page"),
    ACCESS_DENIED_MESSAGE("security.authentication.access-denied",
            "You don't have permission to access this page"),
    NOT_ENOUGH_PERMISSION("security.authentication.not-enough-permission",
            "You do not have enough permission"),
    ERROR_PROCESSING_FILE("security.authentication.processing-file",
            "Error occurred while processing file"),
    INCORRECT_CREDENTIALS("security.authentication.invalid-credentials",
            "User name / password incorrect. Please try again"),
    ACCOUNT_LOCKED("security.authentication.account-lock",
            "Your account has been locked. Please contact administration"),

    INTERNAL_SERVER_ERROR_MSG("security.authentication.internal-server-error",
            "An error occurred while processing the request"),
    METHOD_IS_NOT_ALLOWED("security.authentication.method-not-allowed",
            "This request method is not allowed on this endpoint");

    private String code;

    private String defaultMessageFormat;

    private AppErrorCode(String code, String defaultMessageFormat){
        this.code = code;
        this.defaultMessageFormat = defaultMessageFormat;
    }

    public String code(){
        return this.code;
    }

    public String defaultMessageFormat(){
        return this.defaultMessageFormat;
    }
}
