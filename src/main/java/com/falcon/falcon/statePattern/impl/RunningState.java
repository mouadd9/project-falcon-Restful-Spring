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

@Component("runningStateBean")
public class RunningState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(RunningState.class);

    // starting an already started instance is an invalid operation (it's already running).
    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Attempted to start instance {} which is already RUNNING.", instanceContext.getInstanceId());
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot strat instance %s via cloud provider; it is in RUNNING state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.RUNNING);
    }

    // stopping an instance that is already running is a valid operation.
    // and this Should attempt to stop the instance via the CloudInstanceService.
    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Stopping instance {} from RUNNING state.", instanceContext.getInstanceId());
        // The cloudService.stopInstance method is expected to return CompletableFuture<InstanceActionResponse>
        return cloudService.stopInstance(instanceContext.getInstanceId());
    }

    // terminating an instance that is already running is a valid operation.
    // and this Should attempt to terminate the instance via the CloudInstanceService.
    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService) {
        logger.info("Terminating instance {} from RUNNING state.", instanceContext.getInstanceId());
        // The cloudService.terminateInstance method is expected to return CompletableFuture<InstanceActionResponse>
        return cloudService.terminateInstance(instanceContext.getInstanceId());
    }

    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.RUNNING;
    }
}
