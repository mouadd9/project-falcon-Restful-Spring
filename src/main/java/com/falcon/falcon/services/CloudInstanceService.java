package com.falcon.falcon.services;

import com.falcon.falcon.dtos.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing cloud instances, providing an abstraction over specific cloud providers.
 */
public interface CloudInstanceService {

    /**
     * Creates a new cloud instance.
     *
     * @param machineImageId The identifier of the machine image to use for creating the instance (e.g., AMI ID for AWS).
     * @return A CompletableFuture that will eventually contain the response with details of the created instance.
     */
    CompletableFuture<CreateInstanceResponse> createInstance(String machineImageId, String userId, String operationId, String instanceId);

    /**
     * Stops a specified cloud instance.
     *
     * @param instanceId The identifier of the instance to stop.
     * @return A CompletableFuture that will eventually contain the response indicating the action's outcome.
     */
    CompletableFuture<InstanceActionResponse> stopInstance(String instanceId, String userId, String operationId, String localInstanceId);

    /**
     * Starts a specified cloud instance.
     *
     * @param instanceId The identifier of the instance to start.
     * @return A CompletableFuture that will eventually contain the response indicating the action's outcome.
     */
    CompletableFuture<InstanceActionResponse> startInstance(String instanceId, String userId, String operationId, String localInstanceId);

    /**
     * Terminates a specified cloud instance.
     *
     * @param instanceId The identifier of the instance to terminate.
     * @return A CompletableFuture that will eventually contain the response indicating the action's outcome.
     */
    CompletableFuture<InstanceActionResponse> terminateInstance(String instanceId, String userId, String operationId, String localInstanceId);
}
