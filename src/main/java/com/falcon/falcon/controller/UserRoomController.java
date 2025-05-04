package com.falcon.falcon.controller;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.facadePattern.DomainFacade;
import com.falcon.falcon.service.UserRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class UserRoomController {
    // this Class injects the UserRoomService for User-Room Interaction
    private final UserRoomService userRoomService;
    private final DomainFacade domainFacade;

    public UserRoomController(UserRoomService userRoomService, DomainFacade domainFacade) {
        this.userRoomService = userRoomService;
        this.domainFacade = domainFacade;
    }

    @GetMapping("/{userId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomCatalog(@PathVariable long userId) {
        List<RoomDTO> rooms = domainFacade.getRoomCatalogForUser(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms")
    public ResponseEntity<List<RoomDTO>> getJoinedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getJoinedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/saved-rooms")
    public ResponseEntity<List<RoomDTO>> getSavedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getSavedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/completed-rooms")
    public ResponseEntity<List<RoomDTO>> getCompletedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getCompletedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms/{roomId}")
    public ResponseEntity<RoomDTO> getJoinedRoom(@PathVariable long userId, @PathVariable long roomId) {
        RoomDTO room = userRoomService.getJoinedRoom(userId, roomId);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/{userId}/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.joinRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/save")
    public ResponseEntity<Void> saveRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.saveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.leaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/unsave")
    public ResponseEntity<Void> unSaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.unSaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

}
