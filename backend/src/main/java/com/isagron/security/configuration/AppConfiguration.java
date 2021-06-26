package com.isagron.security.configuration;

import com.isagron.security.configuration.properties.EmailVerificationProperties;
import com.isagron.security.configuration.properties.JwtProperties;
import com.isagron.security.configuration.properties.LoginAttemptProperties;
import com.isagron.security.configuration.properties.SecurityProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AppConfiguration {


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public LoginAttemptProperties loginAttemptProperties(SecurityProperties securityProperties){
        return securityProperties.getLoginAttempts();
    }

    @Bean
    public EmailVerificationProperties emailVerification(SecurityProperties securityProperties){
        return securityProperties.getEmailVerification();
    }

    @Bean
    public JwtProperties jwtProperties(SecurityProperties securityProperties){
        return securityProperties.getJwt();
    }

}
