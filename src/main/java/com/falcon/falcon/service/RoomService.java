package com.falcon.falcon.service;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.exceptions.roomExceptions.RoomAlreadySavedException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;

import java.util.List;

// provides basic room information
public interface RoomService {

    // Done ! ready for tests !!
    List<RoomDTO> getAllRooms();

    RoomDTO getRoomById(Long id) throws RoomNotFoundException;

    RoomDTO createRoom(RoomDTO roomDTO) throws RoomAlreadySavedException;
    // incrementJoinedUsers()
    // decrementJoinedUsers()
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