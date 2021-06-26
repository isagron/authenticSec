package com.isagron.security.services.features.auth;

import com.isagron.security.configuration.properties.SecurityProperties;
import com.isagron.security.domain.dtos.ConfirmTokenRequest;
import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.ReplacePasswordRequest;
import com.isagron.security.domain.entities.ConfirmationToken;
import com.isagron.security.domain.entities.Role;
import com.isagron.security.domain.entities.User;
import com.isagron.security.domain.model.UserPrincipal;
import com.isagron.security.domain.repositories.ConfirmationTokenRepository;
import com.isagron.security.domain.types.DefaultRoleType;
import com.isagron.security.exceptions.ConfirmationTokenNotExistException;
import com.isagron.security.exceptions.InvalidConfirmationTokenException;
import com.isagron.security.services.features.EmailSenderService;
import com.isagron.security.services.features.LoginAttemptService;
import com.isagron.security.services.resources.RoleService;
import com.isagron.security.services.resources.UserService;
import com.isagron.security.services.token_provider.JWTTokenProvider;
import com.isagron.security.services.validators.AppOperation;
import com.isagron.security.services.validators.ValidatorMng;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final SecurityProperties securityProperties;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private final LoginAttemptService loginAttemptService;

    private final ValidatorMng validator;

    private final EmailSenderService emailSenderService;

    private final RoleService roleService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public User login(LoginRequest loginRequest) {

        validator.validate(AppOperation.LOGIN, loginRequest, "Failed to login user");

        if (loginAttemptService.isExceed(loginRequest.getUserName())) {
            userService.lockUser(loginRequest.getUserName());
            loginAttemptService.removeUser(loginRequest.getUserName());
        }

        //throw exception in case of failure
        authenticate(loginRequest.getUserName(), loginRequest.getPassword());

        return userService.findUserByUserName(loginRequest.getUserName())
                .map(user -> {
                    user.setLastLoginDateDisplay(user.getLastLoginDate());
                    user.setLastLoginDate(new Date());
                    return user;
                })
                .orElseThrow(() -> new UsernameNotFoundException(loginRequest.getUserName()));

    }

    private void authenticate(String userName, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userName, password
        ));
    }

    @Override
    public HttpHeaders getJwtHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(securityProperties.getJwt().getTokenHeader(), jwtTokenProvider.generateJwtToken(UserPrincipal.from(user)));
        return headers;
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        validator.validate(AppOperation.REGISTER_USER, registerRequest);
        User user = userService.createUser(registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getUserName(),
                registerRequest.getEmail(),
                encodePassword(registerRequest.getPassword()),
                !securityProperties.getEmailVerification().isEnable(),
                false,
                securityProperties.getEmailVerification().isEnable(),
                DefaultRoleType.ROLE_USER.name(),
                registerRequest.getProfileImageUrl());

        //send confirmation mail
        if (securityProperties.getEmailVerification().isEnable()) {
            confirmUserViaEmail(user);
        }

        return user;
    }

    private void confirmUserViaEmail(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom(securityProperties.getEmailVerification().getFrom());
        mailMessage.setText("Your code: " + confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);
    }

    @Override
    @Transactional
    public User confirmUser(String userName, String code) {
        //find confirmation token
        ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByConfirmationToken(code)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        //compare user name from token to userName @param
        if (confirmationToken.getUser() != null && confirmationToken.getUser().getUserName().equals(userName)) {
            confirmationToken.getUser().setActive(true);
            confirmationToken.getUser().setEmailVerification(true);
            this.confirmationTokenRepository.deleteById(confirmationToken.getId());
            return confirmationToken.getUser();
        }

        throw new InvalidConfirmationTokenException();
    }

    @Override
    @Transactional
    public User replacePassword(ReplacePasswordRequest replacePasswordRequest){
        User user = userService.getOrElseThrow(replacePasswordRequest.getUserName());
        String encodedPassword = encodePassword(replacePasswordRequest.getNewPassword());
        user.setPassword(encodedPassword);
        user.setNeedToReplacePassword(false);
        return user;
    }


    /**
     * In case the user is not login and want to reset the password
     * We send verification code to his mail, he can activate the reset password only with the verifcation code
     * @param email
     */
    @Override
    @Transactional
    public void requestToResetPassword(String email){
        User user = userService.getByEmailOrElseThrow(email);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        user.setNeedToReplacePassword(true);
        emailSenderService.sendEmail(user.getEmail(),
                "Reset Password",
                securityProperties.getEmailVerification().getFrom(),
                "User name: " + user.getUserName() + ",\nCode: " + confirmationToken.getConfirmationToken());
    }



    @Override
    public User resetPassword(ReplacePasswordRequest replacePasswordRequest){
        //find confirmation token
        String code = Optional.ofNullable(replacePasswordRequest.getConfirmation())
                .orElseThrow(ConfirmationTokenNotExistException::new);

        ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByConfirmationToken(code)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        if (confirmationToken.getUser() != null &&
                confirmationToken.getUser().getUserName().equals(replacePasswordRequest.getUserName())) {
            return replacePassword(replacePasswordRequest);
        }

        throw new UsernameNotFoundException(replacePasswordRequest.getUserName());
    }

    @Override
    public void isValidCodeForReset(String userName, String code) {
        //find confirmation token
        ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByConfirmationToken(code)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        //compare user name from token to userName @param
        if (confirmationToken.getUser() != null && confirmationToken.getUser().getUserName().equals(userName)
        && confirmationToken.getUser().isNeedToReplacePassword()) {
            return;
        }

        throw new InvalidConfirmationTokenException();
    }

    private String generatePassword() {
        return UUID.randomUUID().toString();
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }


}
