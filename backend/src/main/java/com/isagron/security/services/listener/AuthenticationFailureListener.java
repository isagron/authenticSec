package com.isagron.security.services.listener;

import com.isagron.security.domain.repositories.UserRepository;
import com.isagron.security.services.features.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;

    private final UserRepository userRepository;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String){
            String userName = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUser(userName);
            userRepository.findUserByUserName(userName)
                    .ifPresent(user -> user.setFailureLogin(user.getFailureLogin() + 1));
        }
    }
}
