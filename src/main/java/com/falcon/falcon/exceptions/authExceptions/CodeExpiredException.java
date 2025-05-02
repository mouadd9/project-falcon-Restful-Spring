package com.falcon.falcon.exceptions.authExceptions;

public class CodeExpiredException extends RuntimeException {
    public CodeExpiredException(String message) {
        super(message);
    }
}
