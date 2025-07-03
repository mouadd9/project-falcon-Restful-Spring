package org.falcon.instanceservice.exceptions;

/**
 * Exception thrown when an error occurs during the provisioning of a cloud instance.
 */
public class InstanceProvisioningException extends RuntimeException {
    
    public InstanceProvisioningException(String message) {
        super(message);
    }
    
    public InstanceProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}
