package com.isagron.security.services.listener;

import com.isagron.security.domain.model.UserPrincipal;
import com.isagron.security.domain.repositories.UserRepository;
import com.isagron.security.services.features.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal){
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.removeUser(user.getUsername());
            userRepository.findUserByUserName(user.getUsername())
                    .ifPresent(persistUser -> persistUser.setSuccessfulLogin(persistUser.getSuccessfulLogin() + 1));
        }
    }
}
