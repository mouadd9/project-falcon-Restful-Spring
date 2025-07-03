package org.falcon.instanceservice.statePattern.impl;


import org.falcon.instanceservice.dto.cloud.InstanceActionResponse;
import org.falcon.instanceservice.entity.Instance;
import org.falcon.instanceservice.enums.InstanceStateEnum;
import org.falcon.instanceservice.exceptions.InvalidInstanceStateException;
import org.falcon.instanceservice.service.CloudInstanceService;
import org.falcon.instanceservice.statePattern.InstanceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class NotStartedState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(NotStartedState.class);

    /**
     * Attempts to start an instance that is in the NOT_STARTED state.
     * This is generally an invalid operation for this state, as the instance
     * needs to be created on the cloud first (which transitions it to RUNNING).
     *
     * @param instanceContext The instance entity (context).
     * @param cloudService    The service used to interact with the cloud provider.
     * @param userId          The ID of the user initiating the operation.
     * @param operationId     The ID of the operation being performed.
     * @return A CompletableFuture completed with a response indicating the operation is not supported for this state.
     */
    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot start instance %s; it is in NOT_STARTED state. Instance must be created on the cloud provider first.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.NOT_STARTED);
    }

    /**
     * Attempts to stop an instance that is in the NOT_STARTED state.
     * This is an invalid operation.
     *
     * @param instanceContext The instance entity (context).
     * @param cloudService    The service used to interact with the cloud provider.
     * @param userId          The ID of the user initiating the operation.
     * @param operationId     The ID of the operation being performed.
     * @return A CompletableFuture completed with a response indicating the operation is not supported for this state.
     */
    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot stop instance %s; it is in NOT_STARTED state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.NOT_STARTED);
    }

    /**
     * Attempts to terminate an instance that is in the NOT_STARTED state.
     * This is an invalid operation as there's no cloud resource to terminate.
     * If the instance record needs to be removed from the database without cloud interaction,
     * that should be a separate service-layer operation (e.g., deleteInstanceRecord).
     *
     * @param instanceContext The instance entity (context).
     * @param cloudService    The service used to interact with the cloud provider.
     * @param userId          The ID of the user initiating the operation.
     * @param operationId     The ID of the operation being performed.
     * @return A CompletableFuture completed with a response indicating the operation is not supported for this state.
     */
    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot terminate instance %s via cloud provider; it is in NOT_STARTED state and likely does not exist on the cloud.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.NOT_STARTED);
    }

    /**
     * Gets the status enum for this state.
     *
     * @return {@link InstanceStateEnum#NOT_STARTED}
     */
    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.NOT_STARTED;
    }
}
