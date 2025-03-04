package com.falcon.falcon.exceptions;

public class VerificationCodeInvalid extends RuntimeException {
    public VerificationCodeInvalid(String message) {
        super(message);
    }
}
