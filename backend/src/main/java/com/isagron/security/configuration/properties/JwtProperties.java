package com.isagron.security.configuration.properties;

import lombok.Data;

@Data
public class JwtProperties {

    private String secret;
    private String tokenPrefix;
    private String issuer;
    private String audience;
    private String tokenHeader;
    private int expirationTimeInMin;

}
