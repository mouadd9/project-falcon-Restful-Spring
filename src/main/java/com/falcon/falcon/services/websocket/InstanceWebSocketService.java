package com.falcon.falcon.services.websocket;

import com.falcon.falcon.dtos.websocket.InstanceOperationUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real-time messaging service for instance operations
 * Handles WebSocket communication to provide live updates to users
 * Optimized for 1:1 user-instance ownership model using direct user messaging
 */
@Service
public class InstanceWebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(InstanceWebSocketService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send operation update directly to the instance owner
     */
    public void sendInstanceUpdateToOwner(String userId, InstanceOperationUpdate update) {
        try {
            messagingTemplate.convertAndSendToUser(userId, "/queue/instance-updates", update);
            logger.info("Sent update to user {}: Instance {} - {}", userId, update.getInstanceId(), update.getStatus());
        } catch (Exception e) {
            logger.error("Failed to send update to user {} for instance {}: {}", userId, update.getInstanceId(), e.getMessage());
        }
    }
    
    /**
     * Generic method to send any operation update to the instance owner
     * Supports multi-phase progress updates with detailed phase information
     */
    public void sendOperationUpdate(String userId, InstanceOperationUpdate update) {
        sendInstanceUpdateToOwner(userId, update);
    }
    
    /**
     * Send operation completion notification to instance owner
     */
    public void sendOperationCompleteToOwner(String userId, String operationId, String instanceId, String ipAddress) {
        InstanceOperationUpdate update = InstanceOperationUpdate.launched(operationId, instanceId, ipAddress);
        sendInstanceUpdateToOwner(userId, update);
    }
    
    /**
     * Send operation failure notification to instance owner
     */
    public void sendOperationErrorToOwner(String userId, String operationId, String instanceId, String message, String error) {
        InstanceOperationUpdate update = InstanceOperationUpdate.failed(operationId, instanceId, message, error);
        sendInstanceUpdateToOwner(userId, update);
    }
    
    /**
     * Send progress update during long-running operations to instance owner
     */
    public void sendProgressUpdateToOwner(String userId, String operationId, String instanceId, 
                                        InstanceOperationUpdate.OperationStatus status, 
                                        String message, Integer progress) {
        InstanceOperationUpdate update = new InstanceOperationUpdate(operationId, instanceId, status, message, progress);
        sendInstanceUpdateToOwner(userId, update);
    }
    
    /**
     * Send custom update with additional data to instance owner
     */
    public void sendCustomUpdateToOwner(String userId, InstanceOperationUpdate update) {
        sendInstanceUpdateToOwner(userId, update);
    }
    
    // /**
    //  * Broadcast update to all connected users (for system-wide notifications)
    //  * Use sparingly - prefer direct user messaging
    //  */
    // public void broadcastInstanceUpdate(InstanceOperationUpdate update) {
    //     String destination = "/topic/instances/global";
        
    //     try {
    //         messagingTemplate.convertAndSend(destination, update);
    //         logger.info("Broadcasted global update: {}", update.getStatus());
    //     } catch (Exception e) {
    //         logger.error("Failed to broadcast update: {}", e.getMessage());
    //     }
    // }
}