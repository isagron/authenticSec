package com.isagron.security.exceptions;

public class InvalidConfirmationTokenException extends SecurityAppException{
    public InvalidConfirmationTokenException() {
        super(AppErrorCode.CONFIRMATION_TOKEN_INVALID, AppErrorCode.CONFIRMATION_TOKEN_INVALID.defaultMessageFormat());
    }
}
