package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.facades.RoomEnrollmentFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller implementing the Sub-Resource Pattern to manage User-Room relationships.
 *
 * <p>This controller handles the many-to-many relationship between Users and Rooms,
 * which is represented by the RoomMembership entity in the domain model. Instead of
 * creating a separate controller for the join entity (RoomMembership), we use the
 * sub-resource pattern to express this relationship in a RESTful way.</p>
 *
 * <p>Sub-Resource Pattern: When a resource (Rooms) needs to be accessed in the context
 * of another resource (Users), we structure endpoints hierarchically as:
 * <code>/users/{userId}/resource-type</code>. This clearly expresses the relationship
 * between parent and child resources.</p>
 *
 * <p>In a many-to-many relationship like User-Room, where:
 * <ul>
 *   <li>A User can join/save multiple Rooms</li>
 *   <li>A Room can have multiple Users</li>
 *   <li>The relationship itself has properties (joined date, completion status, etc.)</li>
 * </ul>
 * This pattern allows us to express operations on the relationship (joining, saving) as
 * well as querying related resources (getting a user's joined rooms).</p>
 *
 * <p>Design Benefits:
 * <ul>
 *   <li>Maintains separation of concerns from generic Room operations</li>
 *   <li>Provides a user-centric view of room-related operations</li>
 *   <li>Creates intuitive endpoints that map to user stories (e.g., "As a user, I want to see my joined rooms")</li>
 *   <li>Models the domain accurately while maintaining RESTful principles</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/users") // resource path
public class RoomEnrollmentController {
    private final RoomEnrollmentFacade roomEnrollmentFacade;

    public RoomEnrollmentController(RoomEnrollmentFacade roomEnrollmentFacade) {
        this.roomEnrollmentFacade = roomEnrollmentFacade;
    }

    @GetMapping("/{userId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomCatalog(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentFacade.getRoomCatalogForUser(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms")
    public ResponseEntity<List<RoomDTO>> getJoinedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentFacade.getJoinedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/saved-rooms")
    public ResponseEntity<List<RoomDTO>> getSavedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentFacade.getSavedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/completed-rooms")
    public ResponseEntity<List<RoomDTO>> getCompletedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentFacade.getCompletedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms/{roomId}")
    public ResponseEntity<RoomDTO> getJoinedRoom(@PathVariable long userId, @PathVariable long roomId) {
        RoomDTO room = roomEnrollmentFacade.getJoinedRoom(userId, roomId);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/{userId}/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentFacade.joinRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/save")
    public ResponseEntity<Void> saveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentFacade.saveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentFacade.leaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/unsave")
    public ResponseEntity<Void> unSaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentFacade.unsaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/rooms/{roomId}/status")
    public ResponseEntity<Map<String, Boolean>> checkRoomStatus(@PathVariable long userId, @PathVariable long roomId) {
        Map<String, Boolean> status = roomEnrollmentFacade.getRoomMembershipStatus(userId, roomId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // claude help here
    // Reset completed challenges to 0 (room membership)
    // find the rows of the correct flag submission related to this user and the challenges of this room and delete them.
    @PostMapping("/{userId}/rooms/{roomId}/reset")
    public ResponseEntity<Void> resetRoomProgress(
            @PathVariable Long userId,
            @PathVariable Long roomId) {
        roomEnrollmentFacade.resetRoomProgress(userId, roomId);
        return ResponseEntity.ok().build();
    }
}
