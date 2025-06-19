package org.falcon.progressionservice.service;

import org.falcon.progressionservice.client.dto.RoomDTO;

import java.util.List;
import java.util.Map;

public interface RoomEnrollmentService {
    // operations on rooms
    void joinRoom(Long userId, Long roomId); // if the room is already saved we will set joinedAt to the existing room membership
    void saveRoom(Long userId, Long roomId); // if the room is already joined we will set isSaved to true and not create the membership.
    void unSaveRoom(Long userId, Long roomId); // if the room is already joined we will set isSaved to false and not create the membership.
    void leaveRoom(Long userId, Long roomId); // if the room is already joined we will set leftAt to the existing room membership
    void resetRoomProgress(Long userId, Long roomId);
    // room retrievals
    List<RoomDTO> getRoomCatalogForUser(Long userId);
    List<RoomDTO> getJoinedRooms(Long userId); // we use the memberships
    List<RoomDTO> getSavedRooms(Long userId); // we use the memberships
    List<RoomDTO> getCompletedRooms(Long userId); // this requires knowing if a room is completed or not
    RoomDTO getJoinedRoom(Long userId, Long roomId); // we use the memberships
    // status retrieval
    Map<String, Boolean> getRoomMembershipStatus(long userId, long roomId); ;
}

