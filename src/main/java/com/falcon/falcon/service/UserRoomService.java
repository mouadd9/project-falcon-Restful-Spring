package com.falcon.falcon.service;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserNotFoundException;

import java.util.List;

public interface UserRoomService {
    // Room membership operations
    void joinRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException; // if the room is already saved we will set joinedAt to the existing room membership
    void saveRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException; // if the room is already joined we will set isSaved to true and not create the membership.
    // Room retrieval operations
    List<RoomDTO> getJoinedRooms(Long userId) throws UserNotFoundException; // we use the memberships
    List<RoomDTO> getSavedRooms(Long userId) throws UserNotFoundException; // we use the memberships
    List<RoomDTO> getCompletedRooms(Long userId) throws UserNotFoundException; // this requires knowing if a room is completed or not
    RoomDTO getJoinedRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException; // we use the memberships
}
