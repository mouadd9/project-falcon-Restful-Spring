package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.ErrorResponse;
import com.falcon.falcon.exceptions.authExceptions.CodeExpiredException;
import com.falcon.falcon.exceptions.authExceptions.EmaiNotVerifiedOrRequestIdNotValid;
import com.falcon.falcon.exceptions.authExceptions.VerificationCodeInvalid;
import com.falcon.falcon.exceptions.challengeExceptions.ChallengeNotFoundException;
import com.falcon.falcon.exceptions.membershipExceptions.RoomMembershipNotFoundException;
import com.falcon.falcon.exceptions.roomExceptions.RoomAlreadySavedException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.RoleNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;
import com.falcon.falcon.exceptions.userExceptions.UserNotFoundException;
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
    // AUTH controller exceptions
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
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 401
                "AUTHENTICATION_FAILED",
                ex.getMessage(),
                request.getDescription(false)
        );

        // Return the ResponseEntity with the error response and HTTP 401 status
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Room Controller exception handlers
    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFoundException(AuthenticationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 401
                "ROOM_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );

        // Return the ResponseEntity with the error response and HTTP 401 status
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomAlreadySavedException.class)
    public ResponseEntity<ErrorResponse> handleRoomAlreadySavedException(AuthenticationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(), // 401
                "ROOM_ALREADY_SAVED",
                ex.getMessage(),
                request.getDescription(false)
        );

        // Return the ResponseEntity with the error response and HTTP 401 status
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(AuthenticationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 401
                "USER_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );

        // Return the ResponseEntity with the error response and HTTP 401 status
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomMembershipNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomMembershipNotFoundException(RoomMembershipNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ROOM_MEMBERSHIP_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChallengeNotFoundException(ChallengeNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "CHALLENGE_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }



}
