package com.falcon.falcon.dtos.websocket;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Real-time progress update DTO for instance operations
 * Sent via WebSocket to provide continuous updates during long-running operations
 */
@Data
public class InstanceOperationUpdate {
    private String operationId;
    private String instanceId;
    private OperationStatus status;
    private String message;
    private Integer progress; // 0-100 percentage
    private LocalDateTime timestamp;
    private String error;
    private String operationType;
    
    // New fields for enhanced progress tracking
    private String ipAddress;       // Instance IP address

    // Enum for operation status
    public enum OperationStatus {
        INITIALIZING,   // Database record creation
        REQUESTING,     // Submitting request to cloud provider
        PROVISIONING,   // Creating AWS resources
        RUNNING,        // Instance is running
        STOPPING,       // Instance is stopping
        STOPPED,        // Instance has stopped
        TERMINATING,    // Instance is being terminated
        FAILED,
        STARTED,
        STARTING,
        TERMINATED   
    }

    // Default constructor
    public InstanceOperationUpdate() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for success updates
    public InstanceOperationUpdate(String operationId, String instanceId, OperationStatus status, String message, Integer progress) {
        this();
        this.operationId = operationId;
        this.instanceId = instanceId;
        this.status = status;
        this.message = message;
        this.progress = progress;
    }

    // Constructor for error updates
    public InstanceOperationUpdate(String operationId, String instanceId, OperationStatus status, String message, String error) {
        this();
        this.operationId = operationId;
        this.instanceId = instanceId;
        this.status = status;
        this.message = message;
        this.error = error;
        this.progress = 0;
    }



    // 1. CREATE INSTANCE UPDATES 

    public static InstanceOperationUpdate initializingCreation(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.INITIALIZING;
        update.message = "Initializing the request for ressources... ";
        update.progress = 10;
        update.operationType = "CREATE"; // Set operationType
        return update;
    }

    public static InstanceOperationUpdate requesting(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.REQUESTING;
        update.message = "Requesting ressources...";
        update.progress = 25;
        update.operationType = "CREATE"; // Set operationType
        return update;
    }

    public static InstanceOperationUpdate provisioning(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.PROVISIONING;
        update.message = "Allocating resources...";
        update.progress = 60;
        update.operationType = "CREATE"; // Set operationType
        return update;
    }

    public static InstanceOperationUpdate launched(String operationId, String instanceId, String ipAddress) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.ipAddress = ipAddress;
        update.status = OperationStatus.RUNNING; // Changed from COMPLETED to RUNNING for clarity
        update.message = "Instance ready and running";
        update.progress = 100;
        update.operationType = "CREATE"; // Set operationType
        return update;
    }


    // 2. STOP INSTANCE UPDATES 

    public static InstanceOperationUpdate stopping(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.STOPPING;
        update.message = "Stopping machine...This may take a while.";
        update.operationType = "STOP"; // Set operationType
        update.progress = 50;
        return update;
    }


    public static InstanceOperationUpdate stopped(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.STOPPED;
        update.message = "Instance stopped successfully, state saved.";
        update.progress = 100;
        update.operationType = "STOP"; 
        return update;
    }


    // 3. START INSTANCE UPDATES 

    public static InstanceOperationUpdate initializingStarting(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.INITIALIZING;
        update.message = "Initializing the request for recovering the machine ... ";
        update.progress = 10;
        update.operationType = "START"; 
        return update;
    }

        // Static factory methods for common scenarios
    public static InstanceOperationUpdate starting(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.STARTING;
        update.message = "Starting machine...";
        update.progress = 25;
        update.operationType = "START"; 
        return update;
    }

    public static InstanceOperationUpdate started(String operationId, String instanceId, String ipAddress) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.ipAddress = ipAddress;
        update.status = OperationStatus.RUNNING;
        update.message = "Instance started, state restored.";
        update.progress = 100;
        update.operationType = "START"; 
        return update;
    }

        // 3. START INSTANCE UPDATES 

        // Static factory methods for common scenarios
    public static InstanceOperationUpdate terminating(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.TERMINATING;
        update.message = "Terminating machine...";
        update.progress = 25;
        update.operationType = "TERMINATE"; 
        return update;
    } 

    public static InstanceOperationUpdate terminated(String operationId, String instanceId) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.TERMINATED;
        update.message = "Instance terminated successfully, resources released.";
        update.progress = 100;
        update.operationType = "TERMINATE"; 
        return update;
    }

    public static InstanceOperationUpdate failed(String operationId, String instanceId, String message, String error) {
        InstanceOperationUpdate update = new InstanceOperationUpdate();
        update.operationId = operationId;
        update.instanceId = instanceId;
        update.status = OperationStatus.FAILED;
        update.message = message;
        update.error = error;
        update.progress = 0;
        // operationType can be set by the caller if specific, or determined by context
        // For a general failure during creation, it would be CREATE.
        // update.operationType = "CREATE"; // Consider if this should always be set or be more dynamic
        return update;
    }
}