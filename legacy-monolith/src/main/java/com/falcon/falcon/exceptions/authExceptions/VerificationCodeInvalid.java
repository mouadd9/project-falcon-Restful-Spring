package com.falcon.falcon.exceptions.authExceptions;

public class VerificationCodeInvalid extends RuntimeException {
    public VerificationCodeInvalid(String message) {
        super(message);
    }
}
