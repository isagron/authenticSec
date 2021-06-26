package com.isagron.security.configuration.properties;

import lombok.Data;

@Data
public class EmailVerificationProperties {

    private boolean enable;

    private String from;
}
