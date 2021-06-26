package com.isagron.security.services.token_provider;

import com.isagron.security.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TokenProvider {
    String generateJwtToken(UserPrincipal userPrincipal);

    List<GrantedAuthority> getAuthorities(String token);

    Authentication getAuthentication(String userName, List<GrantedAuthority> authorities, HttpServletRequest request);

    boolean isTokenValid(String userName, String token);

    String getSubject(String token);
}
