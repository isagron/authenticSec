package com.isagron.security.services.filters;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isagron.security.domain.dtos.ErrorDto;
import com.isagron.security.exceptions.AppErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseEntity<ErrorDto> responseEntity = new ResponseEntity<>(
                ErrorDto.builder()
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .applicationErrorCode(Collections.singletonList(AppErrorCode.ACCESS_DENIED_MESSAGE.code()))
                        .message(AppErrorCode.ACCESS_DENIED_MESSAGE.defaultMessageFormat())
                        .phrase(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .timeStamp(new Date())
                        .build(),
                HttpStatus.UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, responseEntity);
        outputStream.flush();
    }
}
