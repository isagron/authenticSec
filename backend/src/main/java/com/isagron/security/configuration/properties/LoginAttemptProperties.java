package com.isagron.security.configuration.properties;

import lombok.Data;

@Data
public class LoginAttemptProperties {

    private int maximumNumber;

    private int sizeOfCache;

    private long cacheExpireTimeInMin;
}
