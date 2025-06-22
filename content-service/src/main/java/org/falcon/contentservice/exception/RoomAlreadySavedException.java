package org.falcon.contentservice.exception;

public class RoomAlreadySavedException extends RuntimeException {
    public RoomAlreadySavedException(String message) {
        super(message);
    }
}
