package com.falcon.falcon.exceptions.authExceptions;

public class EmaiNotVerifiedOrRequestIdNotValid extends RuntimeException {
    public EmaiNotVerifiedOrRequestIdNotValid(String message) {
        super(message);
    }
}
