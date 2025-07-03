package org.falcon.instanceservice.exceptions;

import lombok.Getter;
import org.falcon.instanceservice.dto.ErrorResponse;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int status;
    private final ErrorResponse errorResponse;

    public ResourceNotFoundException(int status, ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.status = status;
        this.errorResponse = errorResponse;
    }
    // Getters for status and errorResponse
}
