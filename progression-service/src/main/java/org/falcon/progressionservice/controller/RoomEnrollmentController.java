package org.falcon.progressionservice.controller;

import org.falcon.progressionservice.client.dto.RoomDTO;

import org.falcon.progressionservice.service.RoomEnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progression/users") // resource path
public class RoomEnrollmentController {
    private final RoomEnrollmentService roomEnrollmentService;

    public RoomEnrollmentController(RoomEnrollmentService roomEnrollmentService) {
        this.roomEnrollmentService = roomEnrollmentService;
    }

    @GetMapping("/{userId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomCatalog(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentService.getRoomCatalogForUser(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms")
    public ResponseEntity<List<RoomDTO>> getJoinedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentService.getJoinedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/saved-rooms")
    public ResponseEntity<List<RoomDTO>> getSavedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentService.getSavedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/completed-rooms")
    public ResponseEntity<List<RoomDTO>> getCompletedRooms(@PathVariable long userId) {
        List<RoomDTO> rooms = roomEnrollmentService.getCompletedRooms(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/joined-rooms/{roomId}")
    public ResponseEntity<RoomDTO> getJoinedRoom(@PathVariable long userId, @PathVariable long roomId) {
        RoomDTO room = roomEnrollmentService.getJoinedRoom(userId, roomId);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/{userId}/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentService.joinRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/save")
    public ResponseEntity<Void> saveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentService.saveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentService.leaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/rooms/{roomId}/unsave")
    public ResponseEntity<Void> unSaveRoom(@PathVariable long userId, @PathVariable long roomId) {
        roomEnrollmentService.unSaveRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/rooms/{roomId}/status")
    public ResponseEntity<Map<String, Boolean>> checkRoomStatus(@PathVariable long userId, @PathVariable long roomId) {
        Map<String, Boolean> status = roomEnrollmentService.getRoomMembershipStatus(userId, roomId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/{userId}/rooms/{roomId}/reset")
    public ResponseEntity<Void> resetRoomProgress(@PathVariable Long userId, @PathVariable Long roomId) {
        roomEnrollmentService.resetRoomProgress(userId, roomId);
        return ResponseEntity.ok().build();
    }
}
