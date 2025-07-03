package org.falcon.contentservice.exception;

public class ChallengeNotFoundException extends RuntimeException {
    public ChallengeNotFoundException(String message) {
        super(message);
    }
}
