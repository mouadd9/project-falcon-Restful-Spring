package com.falcon.falcon.services;

import com.falcon.falcon.dtos.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.entities.Instance;
import com.falcon.falcon.enums.InstanceStateEnum;

import java.util.concurrent.CompletableFuture;

public interface InstanceService {

    CompletableFuture<CreateInstanceResponse> createAndProvisionInstance(Long roomId, Long userId);

    CompletableFuture<InstanceActionResponse> startInstance(Long internalInstanceId);

    CompletableFuture<InstanceActionResponse> stopInstance(Long internalInstanceId);

    CompletableFuture<InstanceActionResponse> terminateInstance(Long internalInstanceId);

    InstanceStateEnum getInstanceStatus(Long internalInstanceId);

    Instance findInstanceById(Long internalInstanceId);
    
}
