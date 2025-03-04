package com.falcon.falcon.controller;

import com.falcon.falcon.DTOs.ErrorResponse;
import com.falcon.falcon.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// Marks the class as a global exception handler specifically for REST controllers
// Combines @ControllerAdvice and @ResponseBody
// @ResponseBody : is used to indicate that the return value of a method should be bound to the web response body. It tells Spring to serialize the return value directly to the HTTP response body, rather than being interpreted as a view name or model attribute.
/*
@Controller + @ResponseBody = @RestController
@ControllerAdvice + @ResponseBody = @RestControllerAdvice
*/
@RestControllerAdvice
public class GlobalExceptionHandler {
    // here we will handle each Exception throws by the controller or other service class
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "confilct: email taken",
                ex.getMessage(),
                request.getDescription(false)
        );
        // ResponseEntity represents an HTTP response : status code and body
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handler for CodeExpiredException
    @ExceptionHandler(CodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCodeExpiredException(
            CodeExpiredException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request: Code expired",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for VerificationCodeInvalid
    @ExceptionHandler(VerificationCodeInvalid.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeInvalid(
            VerificationCodeInvalid ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request: Invalid verification code",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for EmaiNotVerifiedOrRequestIdNotValid (note the typo in the exception name)
    @ExceptionHandler(EmaiNotVerifiedOrRequestIdNotValid.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedOrRequestIdNotValid(
            EmaiNotVerifiedOrRequestIdNotValid ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request: Email or Request ID invalid",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for RoleNotFoundException
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(
            RoleNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error: Role not found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
