package org.falcon.instanceservice.web;

import org.falcon.instanceservice.dto.ErrorResponse;
import org.falcon.instanceservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class InstanceGlobalExceptionHandler {

    // Instance exception handlers
    @ExceptionHandler(InstanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInstanceNotFoundException(InstanceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "INSTANCE_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InstanceProvisioningException.class)
    public ResponseEntity<ErrorResponse> handleInstanceProvisioningException(InstanceProvisioningException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INSTANCE_PROVISIONING_FAILED",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidInstanceStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInstanceStateException(InvalidInstanceStateException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_INSTANCE_STATE",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InstanceOperationFailedException.class)
    public ResponseEntity<ErrorResponse> handleInstanceOperationFailedException(InstanceOperationFailedException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INSTANCE_OPERATION_FAILED",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InstanceConfigurationException.class)
    public ResponseEntity<ErrorResponse> handleInstanceConfigurationException(InstanceConfigurationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INSTANCE_CONFIGURATION_ERROR",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
