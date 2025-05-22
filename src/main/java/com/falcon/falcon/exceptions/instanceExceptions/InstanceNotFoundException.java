package com.falcon.falcon.exceptions.instanceExceptions;

/**
 * Exception thrown when an instance cannot be found.
 */
public class InstanceNotFoundException extends RuntimeException {
    
    public InstanceNotFoundException(String message) {
        super(message);
    }
    
    public InstanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
