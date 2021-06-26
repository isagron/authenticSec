package com.isagron.security.services.filters;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isagron.security.domain.dtos.ErrorDto;
import com.isagron.security.exceptions.AppErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        ResponseEntity<ErrorDto> responseEntity = new ResponseEntity<>(
                ErrorDto.builder()
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .message(AppErrorCode.FORBIDDEN_MESSAGE.defaultMessageFormat())
                        .applicationErrorCode(Collections.singletonList(AppErrorCode.FORBIDDEN_MESSAGE.code()))
                        .phrase(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .timeStamp(new Date())
                        .build(),
                HttpStatus.FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, responseEntity);
        outputStream.flush();
    }
}
