package org.falcon.instanceservice.exceptions;

/**
 * Exception thrown when an operation on a cloud instance fails.
 */
public class InstanceOperationFailedException extends RuntimeException {
    
    private final String instanceId;
    private final String operation;
    
    public InstanceOperationFailedException(String message, String instanceId, String operation) {
        super(message);
        this.instanceId = instanceId;
        this.operation = operation;
    }
    
    public InstanceOperationFailedException(String message, String instanceId, String operation, Throwable cause) {
        super(message, cause);
        this.instanceId = instanceId;
        this.operation = operation;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public String getOperation() {
        return operation;
    }
}
