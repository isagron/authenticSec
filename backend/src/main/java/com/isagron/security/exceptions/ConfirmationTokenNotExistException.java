package com.isagron.security.exceptions;

public class ConfirmationTokenNotExistException extends SecurityAppException{
    public ConfirmationTokenNotExistException() {
        super(AppErrorCode.CONFIRMATION_TOKEN_NOT_EXIST, AppErrorCode.CONFIRMATION_TOKEN_NOT_EXIST.defaultMessageFormat());
    }
}
