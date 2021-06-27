package com.isagron.security.services.features.auth;

import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.ReplacePasswordRequest;
import com.isagron.security.domain.entities.User;
import org.springframework.http.HttpHeaders;

public interface AuthService {
    User login(LoginRequest loginRequest);

    HttpHeaders getJwtHeader(User user);

    User register(RegisterRequest registerRequest);

    User confirmUser(String userName, String code);

    User replacePassword(ReplacePasswordRequest replacePasswordRequest);

    void requestToResetPassword(String userName);

    User resetPassword(ReplacePasswordRequest replacePasswordRequest);

    void isValidCodeForReset(String userName, String code);
}
