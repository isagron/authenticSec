package com.isagron.security.exceptions;

public class LoginAttemptsExceedException extends SecurityAppException{
    public LoginAttemptsExceedException() {
        super(AppErrorCode.LOGIN_ATTEMPTS_EXCEED, AppErrorCode.LOGIN_ATTEMPTS_EXCEED.defaultMessageFormat());
    }
}
