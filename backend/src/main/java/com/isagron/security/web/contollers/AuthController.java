package com.isagron.security.web.contollers;

import com.isagron.security.domain.dtos.ConfirmTokenRequest;
import com.isagron.security.domain.dtos.EmailWrapperDto;
import com.isagron.security.domain.dtos.LoginRequest;
import com.isagron.security.domain.dtos.RegisterRequest;
import com.isagron.security.domain.dtos.ReplacePasswordRequest;
import com.isagron.security.domain.dtos.UserDto;
import com.isagron.security.domain.entities.User;
import com.isagron.security.services.features.auth.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private ModelMapper modelMapper;

    @Autowired
    public AuthController(ModelMapper modelMapper, AuthService authService) {
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation("Register new user")
    @PostMapping("/register")
    public UserDto register(@Validated @RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return modelMapper.map(user, UserDto.class);
    }

    @ApiOperation("Validate confirmation code")
    @PostMapping("/confirm")
    public UserDto confirm(@Validated @RequestBody ConfirmTokenRequest confirmTokenRequest) {
        User user = authService.confirmUser(confirmTokenRequest.getUserName(), confirmTokenRequest.getCode());
        return modelMapper.map(user, UserDto.class);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Validated @RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        HttpHeaders jwtHeader = authService.getJwtHeader(user);
        return new ResponseEntity<>(modelMapper.map(user, UserDto.class), jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/request-reset-password")
    public void requestResetPassword(@RequestBody EmailWrapperDto email) {
        this.authService.requestToResetPassword(email.getEmail());
    }

    @PostMapping("/is-valid-code-for-reset")
    public void requestResetPassword(@Validated @RequestBody ConfirmTokenRequest confirmTokenRequest) {
        this.authService.isValidCodeForReset(confirmTokenRequest.getUserName(), confirmTokenRequest.getCode());
    }

    @PostMapping("/reset-password")
    public UserDto resetPassword(@RequestBody ReplacePasswordRequest replacePasswordRequest) {
        return this.modelMapper.map(
                this.authService.resetPassword(replacePasswordRequest),
                UserDto.class
        );
    }

    @PostMapping("/replacePassword")
    public UserDto replacePassword(@RequestBody ReplacePasswordRequest replacePasswordRequest) {
        return this.modelMapper.map(
                this.authService.replacePassword(replacePasswordRequest),
                UserDto.class
        );
    }
}
