package org.falcon.contentservice.web;

import org.falcon.contentservice.dto.ErrorResponse;
import org.falcon.contentservice.exception.ChallengeNotFoundException;
import org.falcon.contentservice.exception.RoomAlreadySavedException;
import org.falcon.contentservice.exception.RoomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RoomGlobalExceptionHandler {
    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFoundException(RoomNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                "ROOM_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChallengeNotFoundException(ChallengeNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                "CHALLENGE_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(RoomAlreadySavedException.class)
    public ResponseEntity<ErrorResponse> handleRoomAlreadySavedException(RoomAlreadySavedException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(), // 401
                "ROOM_ALREADY_SAVED",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
