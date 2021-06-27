package com.isagron.security.domain.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

/**
 * This class return when exception occurred during operations,
 * It provide more details for the client to understand the root cause of the error
 */
@Data
@Builder
public class ErrorDto {

    /**
     * Http status, e.g. 400, 409, 500
     */
    private HttpStatus httpStatus;

    /**
     * List of error code, that cause the operation to fail
     */
    private List<String> applicationErrorCode;

    /**
     * General message, relevant to the operation failed
     */
    private String message;

    /**
     * describe the http error code
     */
    private String phrase;

    /**
     * When exception occur
     */
    private Date timeStamp;


}
