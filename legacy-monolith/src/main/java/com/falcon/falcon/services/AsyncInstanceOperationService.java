package com.falcon.falcon.services;

import com.falcon.falcon.dtos.websocket.InstanceOperationStarted;

/**
 * Service responsible for managing asynchronous instance operations
 * This service coordinates between InstanceService, WebSocket messaging, and operation tracking
 * It encapsulates all the business logic for async operations
 */
public interface AsyncInstanceOperationService {
    
    /**
     * Initiate asynchronous instance creation
     * @param roomId The room ID
     * @param userId The user ID
     * @return Immediate response with operation details
     */
    InstanceOperationStarted createInstanceAsync(Long roomId, Long userId);
    
    /**
     * Initiate asynchronous instance start
     * @param instanceId The instance ID
     * @return Immediate response with operation details
     */
    InstanceOperationStarted startInstanceAsync(Long instanceId);
    
    /**
     * Initiate asynchronous instance stop
     * @param instanceId The instance ID
     * @return Immediate response with operation details
     */
    InstanceOperationStarted stopInstanceAsync(Long instanceId);
    
    /**
     * Initiate asynchronous instance termination
     * @param instanceId The instance ID
     * @return Immediate response with operation details
     */
    InstanceOperationStarted terminateInstanceAsync(Long instanceId);
    
 
}
