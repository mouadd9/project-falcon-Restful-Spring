package org.falcon.progressionservice.exception;

import lombok.Getter;
import org.falcon.progressionservice.dto.ErrorResponseDTO;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int status;
    private final ErrorResponseDTO errorResponse;

    public ResourceNotFoundException(int status, ErrorResponseDTO errorResponse) {
        super(errorResponse.getMessage());
        this.status = status;
        this.errorResponse = errorResponse;
    }
    // Getters for status and errorResponse
}
