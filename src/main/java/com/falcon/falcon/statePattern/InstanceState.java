package com.falcon.falcon.statePattern;

import com.falcon.falcon.entities.Instance;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.enums.InstanceStateEnum;
import com.falcon.falcon.services.CloudInstanceService;

import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for different states of a cloud instance.
 * Each concrete implementation of this interface will represent a specific state
 * (e.g., Running, Stopped, Terminated) and define the behavior of the instance
 * when actions are performed upon it in that state.
 */
public interface InstanceState {

    /**
     * Attempts to start the cloud instance.
     * The behavior of this action depends on the current state of the instance.
     *
     * @param instanceContext The instance entity (context) on which the action is performed.
     * @param cloudService    The service used to interact with the cloud provider.
     * @return A CompletableFuture containing the response from the cloud provider.
     */
    CompletableFuture<InstanceActionResponse> startInstance(Instance instanceContext, CloudInstanceService cloudService);

    /**
     * Attempts to stop the cloud instance.
     * The behavior of this action depends on the current state of the instance.
     *
     * @param instanceContext The instance entity (context) on which the action is performed.
     * @param cloudService    The service used to interact with the cloud provider.
     * @return A CompletableFuture containing the response from the cloud provider.
     */
    CompletableFuture<InstanceActionResponse> stopInstance(Instance instanceContext, CloudInstanceService cloudService);

    /**
     * Attempts to terminate the cloud instance.
     * The behavior of this action depends on the current state of the instance.
     *
     * @param instanceContext The instance entity (context) on which the action is performed.
     * @param cloudService    The service used to interact with the cloud provider.
     * @return A CompletableFuture containing the response from the cloud provider.
     */
    CompletableFuture<InstanceActionResponse> terminateInstance(Instance instanceContext, CloudInstanceService cloudService);

    /**
     * Gets the specific {@link InstanceStateEnum} value that this state represents.
     *
     * @return The enum constant representing this state.
     */
    InstanceStateEnum getStatus();
}
