package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.InstanceStateDTO;
import com.falcon.falcon.services.InstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for room-instance related operations.
 * Handles endpoints that deal with instances in the context of rooms.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomInstanceController {
    
    private final InstanceService instanceService;
    
    public RoomInstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }
    
    /**
     * Gets the current instance state for a room and user.
     * Used when loading room details to show current instance status.
     *
     * @param roomId The ID of the room
     * @param userId The ID of the user (from query parameter)
     * @return InstanceStateDTO with current state
     */
    @GetMapping("/{roomId}/instance_details")
    public ResponseEntity<InstanceStateDTO> getInstanceDetailsForRoom(
            @PathVariable Long roomId,
            @RequestParam Long userId) {
        
        InstanceStateDTO instanceState = instanceService.getInstanceStateForRoom(roomId, userId);
        return ResponseEntity.ok(instanceState);
    }
}