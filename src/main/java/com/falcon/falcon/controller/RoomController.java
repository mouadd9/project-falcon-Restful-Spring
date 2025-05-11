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
    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomDetails(@PathVariable Long roomId) {
        RoomDTO roomDTO = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    } // this may generate an exception if the room does not exist (RoomNotFoundException)

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO createdRoom = roomService.createRoom(roomDTO);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    } // this may generate an exception if the room already exists (RoomAlreadySavedException); the AIM and task definition name already exists

}
