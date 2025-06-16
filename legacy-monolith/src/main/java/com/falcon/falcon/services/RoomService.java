package com.falcon.falcon.services;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.exceptions.roomExceptions.RoomAlreadySavedException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;

import java.util.List;

// Room management
public interface RoomService {
    
    List<RoomDTO> getAllRooms(); // fetch all rooms
    RoomDTO getRoomById(Long id) throws RoomNotFoundException; // get room related data
    RoomDTO createRoom(RoomDTO roomDTO) throws RoomAlreadySavedException;
    public void incrementJoinedUsers(Long roomId) throws RoomNotFoundException;
    public void decrementJoinedUsers(Long roomId) throws RoomNotFoundException;
    // incrementRunningInstances()
    // decrementRunningInstances()
}