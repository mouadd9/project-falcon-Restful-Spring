package org.falcon.instanceservice.dto.websocket;

import java.time.LocalDateTime;

/**
 * Immediate response DTO when an instance operation is accepted
 * This is returned via HTTP as confirmation that the operation started
 * The client uses the operationId to track progress via WebSocket
 * the return has the topic /topic/instance/{instanceId}/operations
 * which is used for real-time updates, and the operationId is used to track the operation
 * the client subscribes to that specific topic to receive updates
 */
public class InstanceOperationStarted {
    
    private String operationId; // Unique ID for the operation
    private String status; // Status of the operation (e.g., "ACCEPTED")
    private String message;
    private String estimatedDuration; 
    private String instanceId;
    private String operationType;
    private LocalDateTime timestamp;
    private String websocketTopic;

    // Default constructor
    public InstanceOperationStarted() {}    
    
    // Full constructor
    public InstanceOperationStarted(String operationId, String instanceId, String operationType, String userId) {
        this.operationId = operationId;
        this.instanceId = instanceId;
        this.operationType = operationType;
        this.status = "ACCEPTED";
        this.message = "Operation initiated successfully";
        this.estimatedDuration = "30-60 seconds";
        this.timestamp = LocalDateTime.now();
        // If frontend subscribes to /user/{userId}/queue/instance-updates
        // AND WebSocketSecurityConfig is removed (so convertAndSendToUser won't work as before)
        // then the topic needs to be specific here if the backend is to use convertAndSend to this exact path.
        this.websocketTopic = "/user/" + userId + "/queue/instance-updates"; 
    }

    // Getters and Setters
    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getWebsocketTopic() {
        return websocketTopic;
    }

    public void setWebsocketTopic(String websocketTopic) {
        this.websocketTopic = websocketTopic;
    }
}