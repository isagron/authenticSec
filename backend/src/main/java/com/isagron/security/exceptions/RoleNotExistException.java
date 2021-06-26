package com.isagron.security.exceptions;

public class RoleNotExistException extends SecurityAppException{

    public RoleNotExistException(String roleName) {
        super(AppErrorCode.ROLE_NOT_EXIST, "Role " + roleName + " not exist");
    }
}
