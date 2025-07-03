package org.falcon.instanceservice.service;

import org.falcon.instanceservice.dto.InstanceStateDTO;
import org.falcon.instanceservice.dto.cloud.CreateInstanceResponse;
import org.falcon.instanceservice.dto.cloud.InstanceActionResponse;
import org.falcon.instanceservice.entity.Instance;
import org.falcon.instanceservice.enums.InstanceStateEnum;

import java.util.concurrent.CompletableFuture;

public interface InstanceService {

    CompletableFuture<CreateInstanceResponse> createAndProvisionInstance(Long roomId, Long userId, String operationId);
    Instance findInstanceById(Long internalInstanceId);
    CompletableFuture<InstanceActionResponse> startInstance(Long internalInstanceId, String operationId, String userId);
    CompletableFuture<InstanceActionResponse> stopInstance(Long internalInstanceId, String operationId, String userId);
    CompletableFuture<InstanceActionResponse> terminateInstance(Long internalInstanceId, String operationId, String userId);
    InstanceStateEnum getInstanceStatus(Long internalInstanceId);

    /**
     * Gets the current state of an instance for a given room and user.
     * Returns the instance state if it exists, or a "no instance" state if none exists.
     *
     * @param roomId The ID of the room
     * @param userId The ID of the user
     * @return InstanceStateDTO containing the current state
     */
    InstanceStateDTO getInstanceStateForRoom(Long roomId, Long userId);
}
