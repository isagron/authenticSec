package com.isagron.security.domain.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ErrorDto {

    private HttpStatus httpStatus;

    private List<String> applicationErrorCode;

    private String message;

    private String phrase;

    private Date timeStamp;


}
