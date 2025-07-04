package org.falcon.contentservice.web;

import org.falcon.contentservice.dto.RoomDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.falcon.contentservice.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/content/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<RoomDTO>> getRoomsByIds(@RequestParam(value = "ids") List<Long> roomIds) {
        List<RoomDTO> rooms = roomService.getRoomsByIds(roomIds);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long roomId) {
        RoomDTO roomDTO = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO createdRoom = roomService.createRoom(roomDTO);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @PutMapping("/{roomId}/increment-joined-users")
    public ResponseEntity<Void> incrementJoinedUsers(@PathVariable("roomId") Long roomId) {
        this.roomService.incrementJoinedUsers(roomId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roomId}/decrement-joined-users")
    public ResponseEntity<Void> decrementJoinedUsers(@PathVariable("roomId") Long roomId) {
        this.roomService.decrementJoinedUsers(roomId);
        return ResponseEntity.ok().build();
    }
}
