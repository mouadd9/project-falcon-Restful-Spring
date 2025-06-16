package com.falcon.falcon.exceptions.instanceExceptions;

import com.falcon.falcon.enums.InstanceStateEnum;

/**
 * Exception thrown when an attempt is made to start an instance in an invalid state.
 */
public class InvalidInstanceStateException extends RuntimeException {
    
    private final String instanceId;
    private final String currentState;
    private final String requestedAction;
    
    public InvalidInstanceStateException(String message, String instanceId, String currentState, String requestedAction) {
        super(message);
        this.instanceId = instanceId;
        this.currentState = currentState;
        this.requestedAction = requestedAction;
    }
    
    public InvalidInstanceStateException(String message, InstanceStateEnum currentState) {
        super(message);
        this.instanceId = null;
        this.currentState = currentState.name();
        this.requestedAction = null;
    }
    
    public InvalidInstanceStateException(String message, String instanceId, InstanceStateEnum currentState, String requestedAction) {
        super(message);
        this.instanceId = instanceId;
        this.currentState = currentState.name();
        this.requestedAction = requestedAction;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public String getCurrentState() {
        return currentState;
    }
    
    public String getRequestedAction() {
        return requestedAction;
    }
}
