package org.falcon.instanceservice.service.Imp;

import org.falcon.instanceservice.dto.websocket.InstanceOperationStarted;
import org.falcon.instanceservice.dto.websocket.InstanceOperationUpdate;
import org.falcon.instanceservice.entity.Instance;
import org.falcon.instanceservice.service.AsyncInstanceOperationService;
import org.falcon.instanceservice.service.InstanceService;
import org.falcon.instanceservice.service.websocket.InstanceWebSocketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of AsyncInstanceOperationService
 * This service contains all the business logic for asynchronous instance operations
 * It coordinates between InstanceService, WebSocket messaging, and operation tracking
 */
@Service
public class AsyncInstanceOperationServiceImp implements AsyncInstanceOperationService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceServiceImp.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private InstanceWebSocketService webSocketService;

    @Override
    public InstanceOperationStarted createInstanceAsync(Long roomId, Long userId) {
        String operationId = UUID.randomUUID().toString(); // here we create an operation ID
        logger.info("Starting async instance creation for user {} in room {}, operation ID: {}", userId, roomId, operationId);
        // Start async operation
        instanceService.createAndProvisionInstance(roomId, userId, operationId)
                .thenAccept(createResponse -> {
                    // Send success update via WebSocket to the instance owner
                    // The launched() factory method in InstanceOperationUpdate now sets operationType="CREATE"
                    webSocketService.sendOperationCompleteToOwner(
                            userId.toString(), // user ID as string
                            operationId, // operation ID for tracking
                            createResponse.getInternalInstanceId().toString(), // internal instance ID (e.g., 12345)
                            createResponse.getPrivateIpAddress() // private IP address if available
                    );
                })
                .exceptionally(throwable -> {
                    // Send error update via WebSocket to the instance owner
                    InstanceOperationUpdate errorUpdate = InstanceOperationUpdate.failed( operationId, null,"Failed to create instance", throwable.getMessage());
                    errorUpdate.setOperationType("CREATE"); // Explicitly set for create operation failure
                    webSocketService.sendInstanceUpdateToOwner(userId.toString(), errorUpdate);
                    return null;
                });
        // Return immediate response
        return new InstanceOperationStarted(operationId, null, "CREATE", userId.toString());
    }

    @Override
    public InstanceOperationStarted startInstanceAsync(Long instanceId) {
        String operationId = UUID.randomUUID().toString();
        Instance instance = instanceService.findInstanceById(instanceId);
        String userId = instance.getUserId().toString();

        logger.info("Starting async instance start for instance {} (user {}), operation ID: {}", instanceId, userId, operationId);

        // Start async operation
        instanceService.startInstance(instanceId, operationId, userId)
                .thenAccept(actionResponse -> {
                    // Push update via WebSocket to the instance owner
                    InstanceOperationUpdate update = InstanceOperationUpdate.started(operationId, instanceId.toString(), actionResponse.getPrivateIpAddress());
                    webSocketService.sendOperationUpdate(userId, update);
                })
                .exceptionally(throwable -> {
                    // Send error update via WebSocket to the instance owner
                    InstanceOperationUpdate errorUpdate = InstanceOperationUpdate.failed(operationId, instanceId.toString(), "Failed to start instance", throwable.getMessage());
                    errorUpdate.setOperationType("START"); // Explicitly set for start operation failure
                    webSocketService.sendInstanceUpdateToOwner(userId, errorUpdate);
                    return null;
                });

        // Return immediate response
        return new InstanceOperationStarted(operationId, instanceId.toString(), "START", userId);
    }

    @Override
    public InstanceOperationStarted stopInstanceAsync(Long instanceId) {
        String operationId = UUID.randomUUID().toString();

        // Get the instance to extract the user ID (1:1 ownership model)
        Instance instance = instanceService.findInstanceById(instanceId);
        String userId = instance.getUserId().toString();

        logger.info("Starting async instance stop for instance {} (user {}), operation ID: {}",
                instanceId, userId, operationId);

        // Start async operation
        instanceService.stopInstance(instanceId, operationId, userId)
                .thenAccept(actionResponse -> {
                    // Send success update via WebSocket to the instance owner
                    // Push update via WebSocket to the instance owner
                    InstanceOperationUpdate update = InstanceOperationUpdate.stopped(operationId, instanceId.toString());
                    webSocketService.sendOperationUpdate(userId, update);
                })
                .exceptionally(throwable -> {
                    // Send error update via WebSocket to the instance owner
                    webSocketService.sendOperationErrorToOwner(userId, operationId, instanceId.toString(),
                            "Failed to stop instance", throwable.getMessage());
                    return null;
                });

        // Return immediate response
        return new InstanceOperationStarted(operationId, instanceId.toString(), "STOP", userId);
    }

    @Override
    public InstanceOperationStarted terminateInstanceAsync(Long instanceId) {
        String operationId = UUID.randomUUID().toString();

        // Get the instance to extract the user ID (1:1 ownership model)
        Instance instance = instanceService.findInstanceById(instanceId);
        String userId = instance.getUserId().toString();

        logger.info("Starting async instance termination for instance {} (user {}), operation ID: {}",
                instanceId, userId, operationId);

        // Start async operation
        instanceService.terminateInstance(instanceId, operationId, userId)
                .thenAccept(actionResponse -> {
                    // Send success update via WebSocket to the instance owner
                    InstanceOperationUpdate update = InstanceOperationUpdate.terminated(operationId, instanceId.toString());
                    webSocketService.sendOperationUpdate(userId, update);
                })
                .exceptionally(throwable -> {
                    // Send error update via WebSocket to the instance owner
                    webSocketService.sendOperationErrorToOwner(userId, operationId, instanceId.toString(),
                            "Failed to terminate instance", throwable.getMessage());
                    return null;
                });


        // Return immediate response
        return new InstanceOperationStarted(operationId, instanceId.toString(), "TERMINATE", userId);
    }


}
