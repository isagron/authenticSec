package com.isagron.security.services.features.auth;

import com.isagron.security.configuration.properties.SecurityProperties;
import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.ReplacePasswordRequest;
import com.isagron.security.domain.entities.ConfirmationToken;
import com.isagron.security.domain.entities.User;
import com.isagron.security.domain.model.UserPrincipal;
import com.isagron.security.domain.repositories.ConfirmationTokenRepository;
import com.isagron.security.domain.types.DefaultRoleType;
import com.isagron.security.exceptions.ConfirmationTokenNotExistException;
import com.isagron.security.exceptions.InvalidConfirmationTokenException;
import com.isagron.security.exceptions.LoginAttemptsExceedException;
import com.isagron.security.exceptions.PasswordExpireException;
import com.isagron.security.services.features.EmailSenderService;
import com.isagron.security.services.features.LoginAttemptService;
import com.isagron.security.services.resources.UserService;
import com.isagron.security.services.token_provider.JWTTokenProvider;
import com.isagron.security.services.validators.AppOperation;
import com.isagron.security.services.validators.ValidatorMng;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final PasswordEncoder bCryptPasswordEncoder;

    /**
     * Authenticate user according to user name and password
     * validate login request (user exist, email verified, expire password)
     * validate login attempts not exceed
     * validate if the user doesn't need to renew password
     * Authenticate user user spring boot security chain
     * @param loginRequest - login request including user credential
     * @return - login user
     */
    @Override
    @Transactional
    public User login(LoginRequest loginRequest) {

        validator.validate(AppOperation.LOGIN, loginRequest, "Failed to login user");

        if (loginAttemptService.isExceed(loginRequest.getUserName())) {
            userService.lockUser(loginRequest.getUserName());
            loginAttemptService.removeUser(loginRequest.getUserName());
            throw new LoginAttemptsExceedException();
        }

        if (userService.isPasswordExpire(loginRequest.getUserName(), securityProperties.getPasswordExpirationTimeInHours())){
            throw new PasswordExpireException();
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


    /**
     * Return http header, with token header set
     * @param user - the user from who the token generated
     * @return - http headers element
     */
    @Override
    public HttpHeaders getJwtHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(securityProperties.getJwt().getTokenHeader(), jwtTokenProvider.generateJwtToken(UserPrincipal.from(user)));
        return headers;
    }

    /**
     * Register a new user in the system
     * @param registerRequest - container to properties required for registration
     * @return - register user
     */
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
            confirmUserRegistrationViaEmail(user);
        }

        return user;
    }


    /**
     * Confirm the receive code against the user name according to what saved in the database
     * If code is confirm, the user set back to active and email verification flag set to true
     * The confirmation code remove after confirmation
     * @param userName - the user name associate with the code
     * @param code - the confirmation code
     * @return - the confirmed user
     */
    @Override
    @Transactional
    public User confirmUser(String userName, String code) {
        //find confirmation token
        ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByConfirmationToken(code)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        //compare user name from token to userName @param
        if (confirmationToken.getUser() != null &&
                confirmationToken.getUser().getUserName().equals(userName) &&
        !confirmationToken.isExpire(securityProperties.getConfirmationCodeExpirationInSec())) {
            confirmationToken.getUser().setActive(true);
            confirmationToken.getUser().setEmailVerification(true);
            this.confirmationTokenRepository.deleteById(confirmationToken.getId());
            return confirmationToken.getUser();
        }

        throw new InvalidConfirmationTokenException();
    }

    /**
     * Update the user password value
     * @param replacePasswordRequest - information element for replacing password
     * @return - the user
     */
    @Override
    @Transactional
    public User replacePassword(ReplacePasswordRequest replacePasswordRequest){
        User user = userService.getOrElseThrow(replacePasswordRequest.getUserName());
        String encodedPassword = encodePassword(replacePasswordRequest.getNewPassword());
        user.setPassword(encodedPassword);
        user.setNeedToReplacePassword(false);
        user.setLastTimeRenewPassword(new Date());
        return user;
    }


    /**
     * In case the user is not login and want to reset the password
     * We send verification code to his mail, he can activate the reset password only with the verifcation code
     * @param email - user email
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


    /**
     * Request to reset the user password.
     * This method receive a new password, user name and confirmation code.
     * First it confirm the code assoicate with the user, if the code is valid it call the replace password method
     * @param replacePasswordRequest
     * @return
     */
    @Override
    @Transactional
    public User resetPassword(ReplacePasswordRequest replacePasswordRequest){
        //find confirmation token
        String code = Optional.ofNullable(replacePasswordRequest.getConfirmation())
                .orElseThrow(ConfirmationTokenNotExistException::new);

        ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByConfirmationToken(code)
                .orElseThrow(ConfirmationTokenNotExistException::new);

        if (confirmationToken.getUser() != null &&
                confirmationToken.getUser().getUserName().equals(replacePasswordRequest.getUserName())) {
            this.confirmationTokenRepository.deleteById(confirmationToken.getId());
            return replacePassword(replacePasswordRequest);
        }

        throw new UsernameNotFoundException(replacePasswordRequest.getUserName());
    }

    /**
     * Validate the code and the user with userName @param match
     * @param userName - user name
     * @param code - verification code
     */
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

    /**
     * Encoded password using bCrypt encoder
     * @param password - the password to encrypt
     * @return - encrypted password
     */
    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * authenticate user using spring boot security chain
     * @param userName - the user name
     * @param password - password (not encrypt)
     */
    private void authenticate(String userName, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userName, password
        ));
    }

    /**
     * Send an email to the user with his confirmation code to complete the registration procedure
     * @param user
     */
    private void confirmUserRegistrationViaEmail(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom(securityProperties.getEmailVerification().getFrom());
        mailMessage.setText("Your code: " + confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);
    }

}
