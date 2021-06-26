package com.isagron.security.exceptions;

public class SecurityAppException extends RuntimeException {

    private AppErrorCode errorCode;

    private String[] params;

    public SecurityAppException(AppErrorCode errorCode, String... params){
        super(String.format(errorCode.defaultMessageFormat(), params));
        this.errorCode = errorCode;
        this.params = params;
    }

    public SecurityAppException(AppErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

}
