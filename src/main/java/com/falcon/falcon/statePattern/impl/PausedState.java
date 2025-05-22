package com.falcon.falcon.statePattern.impl;

import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.entities.Instance;
import com.falcon.falcon.enums.InstanceStateEnum;
import com.falcon.falcon.exceptions.instanceExceptions.InvalidInstanceStateException;
import com.falcon.falcon.services.CloudInstanceService;
import com.falcon.falcon.statePattern.InstanceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component("pausedStateBean")
public class PausedState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(PausedState.class);

    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Starting instance {} from PAUSED state.", instanceContext.getInstanceId());
        return cloudService.startInstance(instanceContext.getInstanceId()); // we handle exceptions here !!
    }

    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Attempted to stop instance {} which is already PAUSED (stopped).", instanceContext.getInstanceId());
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot stop instance %s via cloud provider; it is in PAUSED state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.PAUSED);
    }

    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Terminating instance {} from PAUSED state.", instanceContext.getInstanceId());
        return cloudService.terminateInstance(instanceContext.getInstanceId());
    }

    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.PAUSED;
    }
}
