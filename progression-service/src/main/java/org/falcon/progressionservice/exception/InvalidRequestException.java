package org.falcon.progressionservice.exception;

import org.falcon.progressionservice.dto.ErrorResponseDTO;

public class InvalidRequestException extends RuntimeException {
    private final int status;
    private final ErrorResponseDTO errorResponse;
    public InvalidRequestException(int status, ErrorResponseDTO errorResponse) {
        super(errorResponse.getMessage());
        this.status = status;
        this.errorResponse = errorResponse;
    }
}
