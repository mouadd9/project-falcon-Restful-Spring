package org.falcon.progressionservice.service.imp;

import org.falcon.progressionservice.client.ContentServiceClient;
import org.falcon.progressionservice.client.dto.Room;
import org.falcon.progressionservice.dto.RoomDTO;
import org.falcon.progressionservice.entity.RoomMembership;
import org.falcon.progressionservice.mapper.RoomMapper;
import org.falcon.progressionservice.repository.RoomMembershipRepository;
import org.falcon.progressionservice.service.RoomEnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomEnrollmentServiceImp implements RoomEnrollmentService {
    private final RoomMembershipRepository membershipRepository;
    private final ContentServiceClient contentServiceClient; // we will use this to access rooms data (get rooms ...)
    private final RoomMapper roomMapper;

    public RoomEnrollmentServiceImp(
            RoomMembershipRepository membershipRepository,
            ContentServiceClient contentServiceClient,
            RoomMapper roomMapper
    ) {
        this.membershipRepository = membershipRepository;
        this.contentServiceClient = contentServiceClient;
        this.roomMapper = roomMapper;
    }

    @Override
    public void joinRoom(Long userId, Long roomId) {

    }

    @Override
    public void saveRoom(Long userId, Long roomId) {

    }

    @Override
    public void unSaveRoom(Long userId, Long roomId) {

    }

    @Override
    public void leaveRoom(Long userId, Long roomId) {

    }

    @Override
    public void resetRoomProgress(Long userId, Long roomId) {

    }

    @Override
    public List<RoomDTO> getRoomCatalogForUser(Long userId) {
        // step 1 : Get all base room information using roomManager/roomService this service provides us with information regarding rooms NOT USERS
        List<RoomDTO> allRooms = contentServiceClient.getAllRooms();

        // step 2 : Get rooms user has joined or saved  using userManager/userService this service provides us with information regarding users NOT ROOMS (like for example rooms a user has joined !!!)
        List<RoomDTO> joinedRooms = getJoinedRooms(userId); // this method, retrieves a user and its memberships, for each membership it gets the room associated with that membership. it returns a list of roomDTOs
        List<RoomDTO> savedRooms = getSavedRooms(userId);

        // Step 3: Enrich each room with user-specific data
        return allRooms.stream() //
                .map(room -> enrichRoomWithUserData(room, joinedRooms, savedRooms))
                .collect(Collectors.toList());
    }

    private RoomDTO enrichRoomWithUserData(RoomDTO room, List<RoomDTO> joinedRooms, List<RoomDTO> savedRooms) {
        // Check if the room is joined by the user
        joinedRooms.stream()
                .filter(joinedRoom -> joinedRoom.getId().equals(room.getId()))
                .findFirst() // terminal operation
                .ifPresent(joinedRoom -> { // if the
                    room.setIsJoined(joinedRoom.getIsJoined());
                    room.setIsSaved(joinedRoom.getIsSaved()); // this will set is saved to false if the user did not save the room
                    room.setPercentageCompleted(joinedRoom.getPercentageCompleted());
                });

        // Check if the user has saved the room
        savedRooms.stream()
                .filter(r -> r.getId().equals(room.getId()))
                .findFirst()
                .ifPresent(savedRoom -> room.setIsSaved(true));

        return room;
    }

    @Override
    public List<RoomDTO> getJoinedRooms(Long userId) {
        // Memberships for joined rooms
        List<RoomMembership> memberships = membershipRepository.findByUserIdAndIsJoinedTrue(userId);
        // Ids of joined rooms
        List<Long> roomIds = memberships.stream().map(membership -> membership.getRoomId()).toList();
        // joined rooms via open feign client
        List<Room> joinedRooms = contentServiceClient.getRoomsByIds(roomIds);
        // map
        Map<Long, Room> joinedRoomsMap = joinedRooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        // list of roomDTOs
        return memberships.stream().map(membership -> {
            Room room = joinedRoomsMap.get(membership.getRoomId()); // we get the room corresponding to the membership
            return roomMapper.toUserSpecificDTO(room, membership);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getSavedRooms(Long userId) {
        List<RoomMembership> memberships = membershipRepository.findByUserIdAndIsSavedTrue(userId);
        List<Long> roomIds = memberships.stream().map(membership -> membership.getRoomId()).toList();
        List<Room> savedRooms = contentServiceClient.getRoomsByIds(roomIds);
        Map<Long, Room> savedRoomsMap = savedRooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        return memberships.stream().map(membership -> {
            Room room = savedRoomsMap.get(membership.getRoomId());
            return roomMapper.toUserSpecificDTO(room, membership);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getCompletedRooms(Long userId) {
        List<RoomMembership> allMemberships = membershipRepository.findByUserId(userId); // we retrieve all memberships
        List<Long> roomIds = allMemberships.stream().map(membership -> membership.getRoomId()).toList();
        List<Room> rooms = contentServiceClient.getRoomsByIds(roomIds);
        Map<Long, Room> allRoomsMap = rooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        return allMemberships.stream()
                .filter(membership -> {
                    Room room = allRoomsMap.get(membership.getRoomId()); // we extract the room related to the membership
                    if (room == null || room.getTotalChallenges() == 0) {return false;}
                    return membership.getChallengesCompleted() == room.getTotalChallenges();
                })
                .map(membership -> {
                    // After filtering, map the remaining (completed) rooms to the final DTO
                    Room room = allRoomsMap.get(membership.getRoomId());
                    return roomMapper.toUserSpecificDTO(room, membership);
                })
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getJoinedRoom(Long userId, Long roomId) {
        return null;
    }

    @Override
    public Map<String, Boolean> getRoomMembershipStatus(long userId, long roomId) {
        return Map.of();
    }
}
