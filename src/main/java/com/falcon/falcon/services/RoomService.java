package com.falcon.falcon.services;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.exceptions.roomExceptions.RoomAlreadySavedException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;

import java.util.List;

// Room management
public interface RoomService {

    // Done ! ready for tests !!
    List<RoomDTO> getAllRooms(); // fetch all rooms
    RoomDTO getRoomById(Long id) throws RoomNotFoundException; // get room related data
    RoomDTO createRoom(RoomDTO roomDTO) throws RoomAlreadySavedException;
    public void incrementJoinedUsers(Long roomId) throws RoomNotFoundException;
    public void decrementJoinedUsers(Long roomId) throws RoomNotFoundException;
    // incrementRunningInstances()
    // decrementRunningInstances()

    /*

    RoomDTO updateRoom(Long id, RoomDTO roomDTO) throws RoomNotFoundException;

    List<UserDTO> getUsersInRoom(Long roomId) throws RoomNotFoundException;
    int getTotalUsersInRoom(Long roomId) throws RoomNotFoundException;

    List<InstanceDTO> getRunningInstancesInRoom(Long roomId) throws RoomNotFoundException;
    int getTotalRunningInstancesInRoom(Long roomId) throws RoomNotFoundException;

    List<ChallengeDTO> getChallengesInRoom(Long roomId) throws RoomNotFoundException;
    int getTotalChallengesInRoom(Long roomId) throws RoomNotFoundException;
*/
}