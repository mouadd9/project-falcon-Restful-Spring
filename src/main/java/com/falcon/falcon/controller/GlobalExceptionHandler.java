package com.falcon.falcon.controller;

import com.falcon.falcon.DTOs.ErrorResponse;
import com.falcon.falcon.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.core.AuthenticationException;

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
                      HttpStatus.CONFLICT.value(), // 409
                "USER_ALREADY_EXISTS",
                      ex.getMessage(), // An account with this email already exists
                      request.getDescription(false)
        );
        // ResponseEntity represents an HTTP response : status code and body
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handler for CodeExpiredException
    @ExceptionHandler(CodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCodeExpiredException(CodeExpiredException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                "CODE_EXPIRED_OR_WRONG_REQUEST",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for VerificationCodeInvalid
    @ExceptionHandler(VerificationCodeInvalid.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeInvalid(VerificationCodeInvalid ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 409
                "INVALID_CODE",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for EmaiNotVerifiedOrRequestIdNotValid (note the typo in the exception name)
    @ExceptionHandler(EmaiNotVerifiedOrRequestIdNotValid.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedOrRequestIdNotValid(EmaiNotVerifiedOrRequestIdNotValid ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "EMAIL_REQUEST_MISMATCH",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler for RoleNotFoundException
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error: Role not found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
       // String errorCode;
        // Determine specific error code based on the type of AuthenticationException
        /*if (ex instanceof BadCredentialsException) {
            errorCode = "INVALID_CREDENTIALS";
        } else if (ex instanceof LockedException) {
            errorCode = "ACCOUNT_LOCKED";
        } else if (ex instanceof DisabledException) {
            errorCode = "ACCOUNT_DISABLED";
        } else if (ex instanceof AccountExpiredException) {
            errorCode = "ACCOUNT_EXPIRED";
        } else {
            errorCode = "AUTHENTICATION_FAILED";
        }*/

        // Create the error response object
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 401
                "AUTHENTICATION_FAILED",
                ex.getMessage(),
                request.getDescription(false)
        );

        // Return the ResponseEntity with the error response and HTTP 401 status
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }


}
