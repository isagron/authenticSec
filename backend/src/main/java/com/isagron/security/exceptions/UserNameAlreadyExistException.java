package com.isagron.security.exceptions;

public class UserNameAlreadyExistException extends RuntimeException{

    public UserNameAlreadyExistException(String userName){
        super("User name already exist: " + userName);
    }
}
