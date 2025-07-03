package org.falcon.instanceservice.web;

import org.falcon.instanceservice.dto.websocket.InstanceOperationStarted;
import org.falcon.instanceservice.service.AsyncInstanceOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Clean WebSocket Controller: Only handles HTTP requests and responses
 * All business logic is delegated to AsyncInstanceOperationService
 *
 * Purpose: Provide immediate HTTP responses for async instance operations
 * Use Cases:
 * 1. Modern clients that want immediate feedback (no blocking)
 * 2. Real-time progress updates via WebSocket
 * 3. Better user experience with progress indicators
 */
@RestController
@RequestMapping("/api/instances")
public class InstanceAsyncController {

    @Autowired
    private AsyncInstanceOperationService asyncInstanceOperationService;

    /**
     * Use Case: User clicks "Create Instance" - gets immediate response
     * Client subscribes to WebSocket for real-time progress updates
     *
     * Flow:
     * 1. Returns immediate HTTP 200 with operation ID
     * 2. Client connects to WebSocket using operation info
     * 3. Service sends progress updates via WebSocket
     * 4. Client receives completion/failure notification
     */
    @PostMapping("/async")
    public ResponseEntity<InstanceOperationStarted> createInstanceAsync(@RequestParam Long roomId, @RequestParam Long userId) {
        InstanceOperationStarted response = asyncInstanceOperationService.createInstanceAsync(roomId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Use Case: User clicks "Start Instance" - gets immediate response
     * Real-time updates via WebSocket show instance starting progress
     */
    @PostMapping("/{instanceId}/start/async")
    public ResponseEntity<InstanceOperationStarted> startInstanceAsync(@PathVariable Long instanceId) {
        InstanceOperationStarted response = asyncInstanceOperationService.startInstanceAsync(instanceId);
        return ResponseEntity.ok(response);
    }

    /**
     * Use Case: User clicks "Stop Instance" - gets immediate response
     * Real-time updates via WebSocket show instance stopping progress
     */
    @PostMapping("/{instanceId}/stop/async")
    public ResponseEntity<InstanceOperationStarted> stopInstanceAsync(@PathVariable Long instanceId) {
        InstanceOperationStarted response = asyncInstanceOperationService.stopInstanceAsync(instanceId);
        return ResponseEntity.ok(response);
    }

    /**
     * Use Case: User clicks "Terminate Instance" - gets immediate response
     * Real-time updates via WebSocket show instance termination progress
     */
    @DeleteMapping("/{instanceId}/async")
    public ResponseEntity<InstanceOperationStarted> terminateInstanceAsync(@PathVariable Long instanceId) {
        InstanceOperationStarted response = asyncInstanceOperationService.terminateInstanceAsync(instanceId);
        return ResponseEntity.ok(response);
    }
}

