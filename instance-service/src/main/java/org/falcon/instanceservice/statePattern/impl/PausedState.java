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

@Component("pausedStateBean")
public class PausedState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(PausedState.class);

    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Starting instance {} from PAUSED state.", instanceContext.getInstanceId());
        return cloudService.startInstance(instanceContext.getInstanceId(), userId, operationId, instanceContext.getId().toString());
    }

    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Attempted to stop instance {} which is already PAUSED (stopped).", instanceContext.getInstanceId());
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot stop instance %s via cloud provider; it is in PAUSED state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.PAUSED);
    }

    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Terminating instance {} from PAUSED state.", instanceContext.getInstanceId());
        return cloudService.terminateInstance(instanceContext.getInstanceId(), userId, operationId, instanceContext.getId().toString());
    }

    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.PAUSED;
    }
}
