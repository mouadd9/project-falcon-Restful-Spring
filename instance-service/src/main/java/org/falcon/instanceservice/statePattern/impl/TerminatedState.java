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

@Component("terminatedStateBean")
public class TerminatedState implements InstanceState {

    private static final Logger logger = LoggerFactory.getLogger(TerminatedState.class);

    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        String message = String.format("Cannot start instance %s; it is in TERMINATED state.", instanceContext.getInstanceId());
        logger.warn(message);
        
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, instanceContext.getInstanceId(), InstanceStateEnum.TERMINATED, "start");
    }

    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        String message = String.format("Cannot stop instance %s; it is in TERMINATED state.", instanceContext.getInstanceId());
        logger.warn(message);
        
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, instanceContext.getInstanceId(), InstanceStateEnum.TERMINATED, "stop");
    }

    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService, String userId, String operationId) {
        logger.info("Attempted to terminate instance {} which is already TERMINATED.", instanceContext.getInstanceId());
        String instanceIdentifier = instanceContext.getInstanceId() != null ? instanceContext.getInstanceId() : "[DB ID: " + instanceContext.getId() + "]";
        String message = String.format("Cannot terminate instance %s via cloud provider; it is in TERMINATED state.", instanceIdentifier);
        logger.warn(message);
        // Throw an InvalidInstanceStateException to be caught by the global exception handler
        throw new InvalidInstanceStateException(message, InstanceStateEnum.TERMINATED);
    }

    @Override
    public InstanceStateEnum getStatus() {
        return InstanceStateEnum.TERMINATED;
    }
}
