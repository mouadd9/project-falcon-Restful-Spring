package com.falcon.falcon.exceptions;

public class EmaiNotVerifiedOrRequestIdNotValid extends RuntimeException {
    public EmaiNotVerifiedOrRequestIdNotValid(String message) {
        super(message);
    }
}
