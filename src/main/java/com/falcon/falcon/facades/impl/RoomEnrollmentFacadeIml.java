package com.falcon.falcon.facades.impl;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.facades.ChallengeProgressionFacade;
import com.falcon.falcon.facades.RoomEnrollmentFacade;
import com.falcon.falcon.services.RoomService;
import com.falcon.falcon.services.UserRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomEnrollmentFacadeIml implements RoomEnrollmentFacade {
    private final UserRoomService userRoomService;
    private final RoomService roomService;
    private final ChallengeProgressionFacade challengeProgressionFacade;

    public RoomEnrollmentFacadeIml(RoomService roomService,
                                   UserRoomService userRoomService,
                                   ChallengeProgressionFacade challengeProgressionFacade) {
        this.userRoomService = userRoomService;
        this.roomService = roomService;
        this.challengeProgressionFacade = challengeProgressionFacade;
    }

    /**
     * Gets a personalized catalog of all rooms with user-specific information.
     * This migrates the functionality from DomainFacadeImpl.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomCatalogForUser(Long userId) {
        // step 1 : Get all base room information using roomManager/roomService this service provides us with information regarding rooms NOT USERS
        List<RoomDTO> allRooms = roomService.getAllRooms();

        // step 2 : Get rooms user has joined or saved  using userManager/userService this service provides us with information regarding users NOT ROOMS (like for example rooms a user has joined !!!)
        List<RoomDTO> joinedRooms = userRoomService.getJoinedRooms(userId); // this method, retrieves a user and its memberships, for each membership it gets the room associated with that membership. it returns a list of roomDTOs
        List<RoomDTO> savedRooms = userRoomService.getSavedRooms(userId);

        // Step 3: Enrich each room with user-specific data
        return allRooms.stream() //
                .map(room -> enrichRoomWithUserData(room, joinedRooms, savedRooms))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to enrich room data with user-specific information
     */
    private RoomDTO enrichRoomWithUserData(RoomDTO room, List<RoomDTO> joinedRooms, List<RoomDTO> savedRooms) {
        // Check if the room is joined by the user
        joinedRooms.stream()
                .filter(joinedRoom -> joinedRoom.getId().equals(room.getId()))
                .findFirst() // terminal operation
                .ifPresent(joinedRoom -> { // if the
                    room.setIsJoined(joinedRoom.getIsJoined());
                    room.setIsSaved(joinedRoom.getIsSaved()); // this will set is saved to false if the user didnt save the room
                    room.setPercentageCompleted(joinedRoom.getPercentageCompleted());
                });

        // Check if the user has saved the room
        savedRooms.stream()
                .filter(r -> r.getId().equals(room.getId()))
                .findFirst()
                .ifPresent(savedRoom -> room.setIsSaved(true));

        return room;
    }

    // read only transactions
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getJoinedRooms(Long userId) {
        return userRoomService.getJoinedRooms(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getSavedRooms(Long userId) {
        return userRoomService.getSavedRooms(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getCompletedRooms(Long userId) {
        return userRoomService.getCompletedRooms(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDTO getJoinedRoom(Long userId, Long roomId) {
        return userRoomService.getJoinedRoom(userId, roomId);
    }

    @Override
    @Transactional
    public void joinRoom(Long userId, Long roomId) {
        userRoomService.joinRoom(userId, roomId);
    }

    @Override
    @Transactional
    public void saveRoom(Long userId, Long roomId) {
        userRoomService.saveRoom(userId, roomId);
    }

    @Override
    @Transactional
    public void leaveRoom(Long userId, Long roomId) {
        userRoomService.leaveRoom(userId, roomId);
    }

    @Override
    @Transactional
    public void unsaveRoom(Long userId, Long roomId) {
        userRoomService.unSaveRoom(userId, roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Boolean> getRoomMembershipStatus(Long userId, Long roomId) {
        return userRoomService.getRoomMembershipStatus(userId, roomId);
    }

    @Override
    public void resetRoomProgress(Long userId, Long roomId) {
        // Use the challenge progression facade to reset progress
        challengeProgressionFacade.resetChallengeProgress(userId, roomId);
    }
}