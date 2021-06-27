package com.isagron.security.exceptions;

public class PasswordExpireException extends SecurityAppException{
    public PasswordExpireException() {
        super(AppErrorCode.PASSWORD_RESET, AppErrorCode.PASSWORD_RESET.defaultMessageFormat());
    }
}
