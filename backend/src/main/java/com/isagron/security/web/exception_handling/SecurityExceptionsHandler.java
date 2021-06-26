package com.isagron.security.web.exception_handling;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.isagron.security.domain.dtos.ErrorDto;
import com.isagron.security.exceptions.AppErrorCode;
import com.isagron.security.exceptions.EmailAlreadyExistException;
import com.isagron.security.exceptions.InvalidOperationArgumentsException;
import com.isagron.security.exceptions.SecurityAppException;
import com.isagron.security.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Calendar;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class SecurityExceptionsHandler {

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleException(Exception ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Operation failed")
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidOperationArgumentsException.class)
    public ErrorDto handleException(InvalidOperationArgumentsException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .applicationErrorCode(ex.getErrors().getAllErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()))
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SecurityAppException.class)
    public ErrorDto handleException(SecurityAppException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleException(MethodArgumentNotValidException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DisabledException.class)
    public ErrorDto handleException(DisabledException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(AppErrorCode.ACCESS_DENIED_MESSAGE.defaultMessageFormat())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorDto handleException(BadCredentialsException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(AppErrorCode.INCORRECT_CREDENTIALS.defaultMessageFormat())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorDto handleException(AccessDeniedException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(AppErrorCode.NOT_ENOUGH_PERMISSION.defaultMessageFormat())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LockedException.class)
    public ErrorDto handleException(LockedException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(AppErrorCode.ACCOUNT_LOCKED.defaultMessageFormat())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenExpiredException.class)
    public ErrorDto handleException(TokenExpiredException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage().toUpperCase())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ErrorDto handleException(EmailAlreadyExistException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleException(UserNotFoundException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorDto handleException(HttpRequestMethodNotSupportedException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .message(AppErrorCode.METHOD_IS_NOT_ALLOWED.defaultMessageFormat())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResultException.class)
    public ErrorDto handleException(NoResultException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ErrorDto handleException(IOException ex) {
        log.error("Global Exception error handler exception: ", ex);
        return ErrorDto.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .timeStamp(Calendar.getInstance().getTime())
                .build();
    }



}
