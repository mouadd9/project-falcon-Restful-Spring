package org.falcon.contentservice.service;

import org.falcon.contentservice.dto.RoomDTO;

import java.util.List;

public interface RoomService {
    List<RoomDTO> getAllRooms(); // fetch all rooms
    List<RoomDTO> getRoomsByIds(List<Long> roomIds);
    RoomDTO getRoomById(Long id);
    RoomDTO createRoom(RoomDTO roomDTO);
    void incrementJoinedUsers(Long roomId);
    void decrementJoinedUsers(Long roomId);
}
