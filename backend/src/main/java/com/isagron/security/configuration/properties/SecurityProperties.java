package com.isagron.security.configuration.properties;

import com.isagron.security.domain.entities.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("app.security")
@Data
public class SecurityProperties {

    private EmailVerificationProperties emailVerification;

    private LoginAttemptProperties loginAttempts;

    private JwtProperties jwt;

    private List<String> allowedOrigins;
    private List<String> allowedHeaders;

    private List<String> allowedMethods;
    private List<String> exposeHeaders;

    private User firstUser;

    private int passwordExpirationTimeInHours;

    private int confirmationCodeExpirationInSec;

}
