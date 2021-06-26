package com.isagron.security.exceptions;

public class AuthorityAlreadyExistException extends SecurityAppException{

    public AuthorityAlreadyExistException(String authName) {
        super(AppErrorCode.AUTHORITY_ALREADY_EXIST, "Authority " + authName + " already exist");
    }
}
