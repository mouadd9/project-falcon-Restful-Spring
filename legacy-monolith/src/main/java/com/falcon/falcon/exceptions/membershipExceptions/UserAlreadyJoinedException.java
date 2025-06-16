package com.falcon.falcon.exceptions.membershipExceptions;

public class UserAlreadyJoinedException extends RuntimeException {
    public UserAlreadyJoinedException(String message) {
        super(message);
    }
}
