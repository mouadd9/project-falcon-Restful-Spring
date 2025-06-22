package org.falcon.contentservice.service;

import org.falcon.contentservice.dto.RoomDTO;

import java.util.List;

public interface RoomService {
    List<RoomDTO> getAllRooms(); // fetch all rooms
    List<RoomDTO> getRoomsByIds(List<Long> roomIds);
    RoomDTO getRoomById(Long id);
    RoomDTO createRoom(RoomDTO roomDTO);
    public void incrementJoinedUsers(Long roomId);
    public void decrementJoinedUsers(Long roomId);
}
