package com.falcon.falcon.controller;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    // GET /rooms
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // GET /rooms/{id} RequestBody RoomDTO
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId) {
        RoomDTO roomDTO = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    } // this may generate an exception if the room does not exist (RoomNotFoundException)

    // POST /rooms RequestBody RoomDTO
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO createdRoom = roomService.createRoom(roomDTO);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    } // this may generate an exception if the room already exists (RoomAlreadySavedException); the AIM and task definition name already exists

}
