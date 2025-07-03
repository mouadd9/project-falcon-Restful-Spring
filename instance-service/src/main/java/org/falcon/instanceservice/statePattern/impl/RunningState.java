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

@Component("runningStateBean")
public class RunningState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(RunningState.class);

    // starting an already started instance is an invalid operation (it's already running).
    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Attempted to start instance {} which is already RUNNING.", instanceContext.getInstanceId());
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot start instance %s via cloud provider; it is in RUNNING state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.RUNNING);
    }

    // stopping an instance that is already running is a valid operation.
    // and this Should attempt to stop the instance via the CloudInstanceService.
    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Stopping instance {} from RUNNING state.", instanceContext.getInstanceId());
        // The cloudService.stopInstance method is expected to return CompletableFuture<InstanceActionResponse>
        return cloudService.stopInstance(instanceContext.getInstanceId(), userId, operationId, instanceContext.getId().toString());
    }

    // terminating an instance that is already running is a valid operation.
    // and this Should attempt to terminate the instance via the CloudInstanceService.
    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Terminating instance {} from RUNNING state.", instanceContext.getInstanceId());
        // The cloudService.terminateInstance method is expected to return CompletableFuture<InstanceActionResponse>
        return cloudService.terminateInstance(instanceContext.getInstanceId(), userId, operationId, instanceContext.getId().toString());
    }

    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.RUNNING;
    }
}
