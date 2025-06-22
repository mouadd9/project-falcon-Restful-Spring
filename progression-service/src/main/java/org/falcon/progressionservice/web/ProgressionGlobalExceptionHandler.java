package org.falcon.progressionservice.web;

import org.falcon.progressionservice.dto.ErrorResponseDTO;
import org.falcon.progressionservice.exception.ResourceNotFoundException;
import org.falcon.progressionservice.exception.RoomMembershipNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ProgressionGlobalExceptionHandler {
    @ExceptionHandler(RoomMembershipNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoomMembershipNotFoundException(RoomMembershipNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "ROOM_MEMBERSHIP_NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getErrorResponse(), HttpStatus.NOT_FOUND);
    }
}
