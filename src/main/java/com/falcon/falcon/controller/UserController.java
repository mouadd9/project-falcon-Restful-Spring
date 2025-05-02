package com.falcon.falcon.controller;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.facadePattern.DomainFacade;
import com.falcon.falcon.service.UserRoomService;
import com.falcon.falcon.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DomainFacade domainFacade;
    private UserRoomService userRoomService;

    public UserController(DomainFacade domainFacade, UserService userService, UserRoomService userRoomService) {
        this.domainFacade = domainFacade;
        this.userRoomService = userRoomService;
    }
    // GET /users/1/rooms get all rooms for user with id 1
    // The client sends a GET request, e.g., GET /users/123/rooms.
    //The server uses userId (123) to fetch all rooms and adds a status field for each room based on the user’s relationship (e.g., joined or not).
    @GetMapping("/{userId}/rooms")
    public ResponseEntity<List<RoomDTO>> getUserRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = domainFacade.getRoomCatalogForUser(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // GET /users/{userId}/joined-rooms
    // this endpoint returns a list of joined rooms for a user, each joined room has membership details
    @GetMapping("/{userId}/joined-rooms")
    public ResponseEntity<List<RoomDTO>> getJoinedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getJoinedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // GET /users/{userId}/bookmarked-rooms
    // this endpoint returns a list of bookmarked rooms for a user, each bookmarked room has membership details
    @GetMapping("/{userId}/saved-rooms")
    public ResponseEntity<List<RoomDTO>> getSavedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getSavedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // GET /users/{userId}/completed-rooms
    // this endpoint returns a list of completed rooms for a user, each completed room has membership details
    @GetMapping("/{userId}/completed-rooms")
    public ResponseEntity<List<RoomDTO>> getCompletedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = userRoomService.getCompletedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // GET /users/{userId}/joined-room/{roomId}
    // this endpoint returns user specific details about a joined room
    @GetMapping("/{userId}/joined-room/{roomId}")
    public ResponseEntity<RoomDTO> getJoinedRoom(@PathVariable long userId, @PathVariable long roomId) {
        RoomDTO room = userRoomService.getJoinedRoom(userId, roomId);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    // a user can get info about a joined room
    // a user can join a room
    // POST /users/{userId}/rooms/{roomId}/join
    @PostMapping("/{userId}/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.joinRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    // a user can save a room
    // POST /users/{userId}/rooms/{roomId}/save
    @PostMapping("/{userId}/rooms/{roomId}/save")
    public ResponseEntity<Void> saveRoom(@PathVariable long userId, @PathVariable long roomId) {
        userRoomService.saveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

}
