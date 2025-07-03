package org.falcon.instanceservice.service.Imp;

// NOTE :
/*
 * when your lambda returns another CompletableFuture, use thenCompose to avoid nesting.
 * When it returns a direct value, use thenApply.
 */

import org.falcon.instanceservice.client.ContentServiceClient;
import org.falcon.instanceservice.client.dto.RoomDTO;
import org.falcon.instanceservice.dto.InstanceStateDTO;
import org.falcon.instanceservice.dto.cloud.CreateInstanceResponse;
import org.falcon.instanceservice.dto.cloud.InstanceActionResponse;
import org.falcon.instanceservice.dto.websocket.InstanceOperationUpdate;
import org.falcon.instanceservice.entity.Instance;
import org.falcon.instanceservice.enums.InstanceStateEnum;
import org.falcon.instanceservice.exceptions.InstanceConfigurationException;
import org.falcon.instanceservice.exceptions.InstanceNotFoundException;
import org.falcon.instanceservice.exceptions.InstanceOperationFailedException;
import org.falcon.instanceservice.exceptions.InstanceProvisioningException;
import org.falcon.instanceservice.repository.InstanceRepository;
import org.falcon.instanceservice.service.CloudInstanceService;
import org.falcon.instanceservice.service.InstanceService;
import org.falcon.instanceservice.service.websocket.InstanceWebSocketService;
import org.falcon.instanceservice.statePattern.InstanceState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// This is the "orchestrator" that coordinates database, cloud, and WebSocket operations
@Service
public class InstanceServiceImp implements InstanceService {
    private static final Logger logger = LoggerFactory.getLogger(InstanceServiceImp.class);

    private final InstanceRepository instanceRepository; // to fetch and save instance data
    private final Map<InstanceStateEnum, InstanceState> stateMap; // this map contains all state objects, we will use it
    // to get the current state object of the instance,
    // this is a configured bean
    private final CloudInstanceService cloudInstanceService; // to interact with the cloud provider
    private final InstanceWebSocketService webSocketService; // to send real-time updates

    private final ContentServiceClient contentServiceClient;
    public InstanceServiceImp(InstanceRepository instanceRepository,
                              Map<InstanceStateEnum, InstanceState> stateMap,
                              CloudInstanceService cloudInstanceService,
                              InstanceWebSocketService webSocketService,
                              ContentServiceClient contentServiceClient) {
        this.instanceRepository = instanceRepository;
        this.stateMap = stateMap;
        this.cloudInstanceService = cloudInstanceService;
        this.webSocketService = webSocketService;
        this.contentServiceClient = contentServiceClient;
    }

    // @Transactional
    @Override
    public CompletableFuture<CreateInstanceResponse> createAndProvisionInstance(Long roomId, Long userId,
                                                                                String operationId) {
        // 1. INITIALIZING (10% progress) - Send initial progress update
        // Factory method InstanceOperationUpdate.initializing now sets operationType="CREATE"
        webSocketService.sendCustomUpdateToOwner(
                userId.toString(),
                InstanceOperationUpdate.initializingCreation(operationId, "pending"));

        RoomDTO room = this.contentServiceClient.getRoomById(roomId);
        String amiId = room.getAmiId();

        // if the amiID is absent we raise an exception
        if (amiId == null || amiId.isEmpty()) {
            String errorMessage = "AMI ID is not configured for room: " + roomId;
            logger.error(errorMessage);

            // Send error update
            InstanceOperationUpdate errorUpdate = InstanceOperationUpdate.failed(
                    operationId, "pending", "Configuration Error", errorMessage);
            errorUpdate.setOperationType("CREATE"); // Set operationType for this failure
            webSocketService.sendInstanceUpdateToOwner(userId.toString(), errorUpdate);

            throw new InstanceProvisioningException(errorMessage);
        }

        // else if the amiID exists we create a new instance in the DB with the state
        // NOT_STARTED
        Instance newInstance = Instance.builder()
                .userId(userId)
                .roomId(roomId)
                .instanceState(InstanceStateEnum.NOT_STARTED)
                .launchDate(new Date())
                .build();

        Instance savedInstance = instanceRepository.save(newInstance);
        logger.info("Instance record created with internal ID: {} in {} state for user ID {} and room ID {}.",
                savedInstance.getId(), savedInstance.getInstanceState(), userId, roomId);

        String instanceId = savedInstance.getId().toString();

        // 2. REQUESTING (25% progress) - Database record created, now submitting to AWS
        // Factory method InstanceOperationUpdate.requesting now sets operationType="CREATE"
        webSocketService.sendCustomUpdateToOwner(
                userId.toString(),
                InstanceOperationUpdate.requesting(operationId, instanceId)
        );

        // we call the cloud provider to provision the instance
        // and we pass the amiID to it
        return cloudInstanceService.createInstance(amiId, userId.toString(), operationId, instanceId)
                .thenApply(createResponse -> {
                    // the response from the cloud provider contains the instance ID and the private
                    // IP address of the VM
                    if (createResponse.getInstanceId() != null && !createResponse.getInstanceId().isEmpty()) {

                        String cloudInstanceId = createResponse.getInstanceId().toString();
                        logger.info("Cloud instance {} successfully provisioned for local instance ID {}. IP: {}",
                                cloudInstanceId, savedInstance.getId(), createResponse.getPrivateIpAddress());

                        // update the instance in the DB with the cloud instance ID and IP address,
                        // (this is the only place we update the instance in the DB)
                        savedInstance.setInstanceId(createResponse.getInstanceId());
                        savedInstance.setIpAddress(createResponse.getPrivateIpAddress());
                        savedInstance.setInstanceState(InstanceStateEnum.RUNNING);
                        Instance fullyProvisionedInstance = instanceRepository.save(savedInstance);
                        logger.info("Instance ID {} (Cloud ID {}) successfully transitioned to {} state in DB.",
                                fullyProvisionedInstance.getId(), fullyProvisionedInstance.getInstanceId(),
                                fullyProvisionedInstance.getInstanceState());
                        // if we reached this point, the provisioning was successful and the instance is
                        // persisted in the DB and we are ready to return a reponse
                        createResponse.setInternalInstanceId(fullyProvisionedInstance.getId());
                        createResponse.setInstanceState(fullyProvisionedInstance.getInstanceState());
                        return createResponse;
                    } else {
                        // if the reponse id not valid we throw an exception
                        String errorMessage = "Cloud instance provisioning failed for local instance ID "
                                + savedInstance.getId() + ". No instance ID returned.";
                        logger.error(errorMessage);

                        // Send error update
                        InstanceOperationUpdate errorUpdate = InstanceOperationUpdate.failed(
                                operationId, instanceId, "Provisioning Failed", errorMessage);
                        errorUpdate.setOperationType("CREATE"); // Set operationType for this failure
                        webSocketService.sendInstanceUpdateToOwner(userId.toString(), errorUpdate);

                        throw new InstanceProvisioningException(errorMessage);
                    }
                }).exceptionally(ex -> {
                    // if the provisioning fails we log the error and throw an exception
                    // we use the savedInstance ID to identify the instance in the DB
                    // ex.getMessage() will contain the error message from the cloud provider
                    String errorMessage = "Exception during cloud instance provisioning for local instance ID "
                            + savedInstance.getId() + ": " + ex.getMessage();
                    logger.error(errorMessage, ex);

                    // Send error update
                    InstanceOperationUpdate errorUpdate = InstanceOperationUpdate.failed(
                            operationId, instanceId, "Provisioning Exception", errorMessage);
                    errorUpdate.setOperationType("CREATE"); // Set operationType for this failure
                    webSocketService.sendInstanceUpdateToOwner(userId.toString(), errorUpdate);

                    throw new InstanceProvisioningException(errorMessage, ex);
                });
    }

    @Override
    public Instance findInstanceById(Long internalInstanceId) {
        return instanceRepository.findById(internalInstanceId)
                .orElseThrow(() -> new InstanceNotFoundException("Instance not found with id: " + internalInstanceId));
    }

    // this method is used to get the current state object of the instance, we pass
    // to it the instance and it gets the proper state object from the map using the
    // instance state
    private InstanceState getCurrentState(Instance instance) {
        InstanceState currentState = stateMap.get(instance.getInstanceState());
        if (currentState == null) {
            String errorMsg = "Configuration error: No state object for " + instance.getInstanceState()
                    + " for instance ID " + instance.getId();
            logger.error(errorMsg);
            throw new InstanceConfigurationException(errorMsg);
        }
        return currentState;
    }

    // we have three actions to perform on the created instances
    // 1. startInstance: this method is used to start an instance that is already
    // created and provisioned on the cloud provider
    // 2. stopInstance: this method is used to stop an instance that is already
    // created and provisioned on the cloud provider
    // 3. terminateInstance: this method is used to terminate an instance that is
    // already created and provisioned on the cloud provider
    @Override
    @Transactional
    public CompletableFuture<InstanceActionResponse> startInstance(Long internalInstanceId, String operationId, String userId) {

        webSocketService.sendCustomUpdateToOwner(
                userId,
                InstanceOperationUpdate.initializingStarting(operationId, internalInstanceId.toString())
        );

        Instance instance = findInstanceById(internalInstanceId); // this could generate an exception if the instance is
        // not found
        InstanceState currentState = getCurrentState(instance);

        logger.info("Attempting to start instance ID {} (Cloud ID {}) from state {}. Operation ID: {}",
                internalInstanceId, instance.getInstanceId(), instance.getInstanceState(), operationId);

        // 2. REQUESTING (25% progress) - Database record created, now submitting to AWS
        // Factory method InstanceOperationUpdate.requesting now sets operationType="CREATE"
        webSocketService.sendCustomUpdateToOwner(
                userId,
                InstanceOperationUpdate.requesting(operationId,  internalInstanceId.toString())
        );

        return currentState.startInstance(instance, cloudInstanceService, userId, operationId)
                .thenApply(actionResponse -> {

                    instance.setInstanceState(InstanceStateEnum.RUNNING);

                    instanceRepository.save(instance);

                    logger.info("Instance ID {} (Cloud ID {}) successfully started and transitioned to {} state.", internalInstanceId, instance.getInstanceId(), instance.getInstanceState());

                    return actionResponse;

                }).exceptionally(ex -> {

                    logger.error("Exception while trying to start instance ID {} (Cloud ID {}): {}", internalInstanceId,
                            instance.getInstanceId(), ex.getMessage(), ex);
                    // If the exception is a CompletionException, we want to get the cause of it
                    throw new InstanceOperationFailedException(
                            "Failed to start instance: " + ex.getMessage(),
                            instance.getInstanceId(),
                            "start");
                });
    }

    @Override
    @Transactional
    public CompletableFuture<InstanceActionResponse> stopInstance(Long internalInstanceId, String operationId, String userId) {

        webSocketService.sendCustomUpdateToOwner(
                userId,
                InstanceOperationUpdate.stopping(operationId, internalInstanceId.toString())
        );

        Instance instance = findInstanceById(internalInstanceId);
        InstanceState currentState = getCurrentState(instance);

        logger.info("Attempting to stop instance ID {} (Cloud ID {}) from state {}. Operation ID: {}",
                internalInstanceId, instance.getInstanceId(), instance.getInstanceState(), operationId);

        return currentState.stopInstance(instance, cloudInstanceService, userId, operationId)
                .thenApply(actionResponse -> {

                    instance.setInstanceState(InstanceStateEnum.PAUSED);
                    instanceRepository.save(instance);

                    logger.info("Instance ID {} (Cloud ID {}) successfully stopped and transitioned to {} state.",
                            internalInstanceId, instance.getInstanceId(), instance.getInstanceState());

                    return actionResponse;
                }).exceptionally(ex -> {
                    logger.error("Exception while trying to stop instance ID {} (Cloud ID {}): {}", internalInstanceId,
                            instance.getInstanceId(), ex.getMessage(), ex);
                    throw new InstanceOperationFailedException(
                            "Failed to stop instance: " + ex.getMessage(),
                            instance.getInstanceId(),
                            "stop");
                });
    }

    @Override
    @Transactional
    public CompletableFuture<InstanceActionResponse> terminateInstance(Long internalInstanceId, String operationId, String userId) {
        webSocketService.sendCustomUpdateToOwner(
                userId,
                InstanceOperationUpdate.terminating(operationId, internalInstanceId.toString())
        );
        Instance instance = findInstanceById(internalInstanceId);
        InstanceState currentState = getCurrentState(instance);

        logger.info("Attempting to terminate instance ID {} (Cloud ID {}) from state {}. Operation ID: {}",
                internalInstanceId, instance.getInstanceId(), instance.getInstanceState(), operationId);

        return currentState.terminateInstance(instance, cloudInstanceService, userId, operationId)
                .thenApply(actionResponse -> {
                    // DELETE the instance from database instead of just updating state
                    logger.info("Instance ID {} (Cloud ID {}) successfully terminated on AWS. Deleting from database...",
                            internalInstanceId, instance.getInstanceId());

                    // Delete the instance record from database
                    instanceRepository.delete(instance);

                    logger.info("Instance ID {} (Cloud ID {}) successfully deleted from database.",
                            internalInstanceId, instance.getInstanceId());

                    return actionResponse;
                }).exceptionally(ex -> {
                    logger.error("Exception while trying to terminate instance ID {} (Cloud ID {}): {}",
                            internalInstanceId, instance.getInstanceId(), ex.getMessage(), ex);
                    throw new InstanceOperationFailedException(
                            "Failed to terminate instance: " + ex.getMessage(),
                            instance.getInstanceId(),
                            "terminate");
                });
    }

    @Override
    public InstanceStateEnum getInstanceStatus(Long internalInstanceId) {
        Instance instance = findInstanceById(internalInstanceId);
        logger.debug("Fetching status for instance ID {} (Cloud ID {}): {}", internalInstanceId,
                instance.getInstanceId(), instance.getInstanceState());
        return instance.getInstanceState();
    }

    @Override
    public InstanceStateDTO getInstanceStateForRoom(Long roomId, Long userId) {
        logger.info("Getting instance state for room {} and user {}", roomId, userId);

        try {
            // Find the user's instance for this room
            // You'll need to add this method to your repository or use existing query methods
            Optional<Instance> instanceOpt = instanceRepository.findByRoomIdAndUserId(roomId, userId);

            if (instanceOpt.isPresent()) {
                Instance instance = instanceOpt.get();
                logger.info("Found instance {} in state {} for room {} and user {}",
                        instance.getId(), instance.getInstanceState(), roomId, userId);
                return InstanceStateDTO.fromInstance(instance);
            } else {
                logger.info("No instance found for room {} and user {}", roomId, userId);
                return InstanceStateDTO.noInstance(roomId.toString());
            }
        } catch (Exception ex) {
            logger.error("Error getting instance state for room {} and user {}: {}",
                    roomId, userId, ex.getMessage(), ex);
            // Return a safe default state
            return InstanceStateDTO.builder()
                    .roomId(roomId.toString())
                    .lifecycleStatus(InstanceStateEnum.NOT_STARTED)
                    .message("Error loading instance state: " + ex.getMessage())
                    .lastStatusUpdateAt(new Date())
                    .build();
        }
    }
}
