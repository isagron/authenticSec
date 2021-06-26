package com.isagron.security.services.validators;

import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.domain.dtos.UpdateUserRequest;
import com.isagron.security.domain.dtos.UserPropertiesRequest;

public enum AppOperation {

    REGISTER_USER(RegisterRequest.class, "Failed to register new user"),
    UPDATE_USER(UpdateUserRequest.class, "Failed to update user information"),
    LOGIN(LoginRequest.class, "Failed to login user"),
    ADD_USER(UserPropertiesRequest .class, "Failed to add user"),
    CREATE_ROLE(RoleDto.class, "Failed to create role");

    private Class<?> cls;

    private String failureMessage;

    private AppOperation(Class<?> className, String failureMessage){
        this.cls = className;
        this.failureMessage = failureMessage;
    }


    public String className(){
        return this.cls.getName();
    }

    public Class<?> getParameterClass() {
        return cls;
    }
}
