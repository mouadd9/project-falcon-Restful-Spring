package org.falcon.instanceservice.exceptions;


import org.falcon.instanceservice.dto.ErrorResponse;

public class InvalidRequestException extends RuntimeException {
    private final int status;
    private final ErrorResponse errorResponse;
    public InvalidRequestException(int status, ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.status = status;
        this.errorResponse = errorResponse;
    }
}
