package com.isagron.security.exceptions;

public class VerificationException extends SecurityAppException{

    public VerificationException(){
        super(AppErrorCode.VERIFICATION_FAILURE, AppErrorCode.VERIFICATION_FAILURE.defaultMessageFormat());
    }
}
