package org.falcon.instanceservice.exceptions;

/**
 * Exception thrown when an instance resource is not properly configured.
 */
public class InstanceConfigurationException extends RuntimeException {
    
    public InstanceConfigurationException(String message) {
        super(message);
    }
    
    public InstanceConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
