package com.isagron.security.exceptions;

public class AuthorityNotExistException extends SecurityAppException{

    public AuthorityNotExistException(String authName) {
        super(AppErrorCode.AUTHORITY_NOT_EXIST, "Authority " + authName + " not exist");
    }
}
