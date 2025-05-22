package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.services.InstanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for instance management operations.
 */
@RestController
@RequestMapping("/api/instances")
public class InstanceController {
    private final InstanceService instanceService;
    
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * Create and provision a new instance.
     *
     * @param roomId The room ID to associate the instance with
     * @param userId The user ID requesting the instance
     * @return A future with the instance creation response
     */    
    @PostMapping
    public ResponseEntity<CompletableFuture<CreateInstanceResponse>> createAndProvisionInstance(@RequestParam Long roomId, @RequestParam Long userId) {
        CompletableFuture<CreateInstanceResponse> instanceFuture = instanceService.createAndProvisionInstance(roomId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(instanceFuture);
    }

    /**
     * Start an existing instance.
     *
     * @param instanceId The database ID of the instance
     * @return A future with the instance action response or 404 if not found
     */
    @PostMapping("/{instanceId}/start")
    public ResponseEntity<CompletableFuture<InstanceActionResponse>> startInstance(@PathVariable Long instanceId) {
        CompletableFuture<InstanceActionResponse> actionResponseFuture = instanceService.startInstance(instanceId);
        return ResponseEntity.ok(actionResponseFuture);
    }

    /**
     * Stop (pause) an existing instance.
     *
     * @param instanceId The database ID of the instance
     * @return A future with the instance action response or 404 if not found
     */
    @PostMapping("/{instanceId}/stop")
    public ResponseEntity<CompletableFuture<InstanceActionResponse>> stopInstance(@PathVariable Long instanceId) {
        CompletableFuture<InstanceActionResponse> actionResponseFuture = instanceService.stopInstance(instanceId);
        return ResponseEntity.ok(actionResponseFuture);
    }

    /**
     * Terminate an existing instance.
     *
     * @param instanceId The database ID of the instance
     * @return A future with the instance action response or 404 if not found
     */
    @DeleteMapping("/{instanceId}")
    public ResponseEntity<CompletableFuture<InstanceActionResponse>> terminateInstance(@PathVariable Long instanceId) {
        CompletableFuture<InstanceActionResponse> actionResponseFuture = instanceService.terminateInstance(instanceId);
        return ResponseEntity.ok(actionResponseFuture);
    }
}
