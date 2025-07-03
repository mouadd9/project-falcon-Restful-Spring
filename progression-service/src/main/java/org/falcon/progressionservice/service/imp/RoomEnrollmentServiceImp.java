package org.falcon.progressionservice.service.imp;

import org.falcon.progressionservice.client.ContentServiceClient;
import org.falcon.progressionservice.client.dto.ChallengeDTO;
import org.falcon.progressionservice.client.dto.RoomDTO;
import org.falcon.progressionservice.entity.RoomMembership;
import org.falcon.progressionservice.exception.RoomMembershipNotFoundException;
import org.falcon.progressionservice.mapper.RoomMapper;
import org.falcon.progressionservice.repository.FlagSubmissionRepository;
import org.falcon.progressionservice.repository.RoomMembershipRepository;
import org.falcon.progressionservice.service.FlagSubmissionService;
import org.falcon.progressionservice.service.RoomEnrollmentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomEnrollmentServiceImp implements RoomEnrollmentService {
    private final ContentServiceClient contentServiceClient; // we will use this to access rooms data (get rooms ...)
    private final RoomMapper roomMapper;
    private final RoomMembershipRepository roomMembershipRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final FlagSubmissionService flagSubmissionService;

    public RoomEnrollmentServiceImp(
            ContentServiceClient contentServiceClient,
            RoomMapper roomMapper,
            RoomMembershipRepository roomMembershipRepository,
            FlagSubmissionRepository flagSubmissionRepository,
            FlagSubmissionService flagSubmissionService
    ) {
        this.contentServiceClient = contentServiceClient;
        this.roomMapper = roomMapper;
        this.roomMembershipRepository = roomMembershipRepository;
        this.flagSubmissionRepository = flagSubmissionRepository;
        this.flagSubmissionService = flagSubmissionService;
    }

    @Override
    public void joinRoom(Long userId, Long roomId) {
        // step 1 : this returns an optional (this may or may not have a roomMembership)
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByUserIdAndRoomId(userId, roomId);
        // step 2 : check
        roomMembership.ifPresentOrElse(
                membership -> {
                    this.contentServiceClient.incrementJoinedUsers(roomId);
                    membership.setIsJoined(true); // we set isJoined to true
                    this.roomMembershipRepository.save(membership); // we persist it
                },() -> {
                    // User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found")); // we first retrieve the user
                    // Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("room not found")); // then the room
                    RoomMembership newRoomMembership = new RoomMembership();
                    newRoomMembership.setRoomId(roomId);
                    newRoomMembership.setUserId(userId);
                    newRoomMembership.setIsSaved(false);
                    newRoomMembership.setIsJoined(true);
                    this.roomMembershipRepository.save(newRoomMembership);
                    this.contentServiceClient.incrementJoinedUsers(roomId);
                }
        );
    }

    @Override
    public void saveRoom(Long userId, Long roomId) {
        // step 1
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByUserIdAndRoomId(userId, roomId);
        // step 2
        roomMembership.ifPresentOrElse(
                membership -> {
                    membership.setIsSaved(true);
                    this.roomMembershipRepository.save(membership);
                }, () -> {
                    // User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found")); // we first retrieve the user
                    // Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("room not found")); // then the room
                    RoomMembership newRoomMembership = new RoomMembership();
                    newRoomMembership.setRoomId(roomId);
                    newRoomMembership.setUserId(userId);
                    newRoomMembership.setIsJoined(false);
                    newRoomMembership.setIsSaved(true);
                    this.roomMembershipRepository.save(newRoomMembership);
                }
        );
    }

    @Override
    public void unSaveRoom(Long userId, Long roomId) {
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByUserIdAndRoomId(userId, roomId);
        roomMembership.ifPresentOrElse(membership -> {
            if (membership.getIsJoined()) { // if the room is Joined, then we will, set isSaved to false
                membership.setIsSaved(false);
                this.roomMembershipRepository.save(membership);
            } else {
                this.roomMembershipRepository.delete(membership);
            }
        }, () -> {
            throw new RoomMembershipNotFoundException("User has not saved this room"); // Handle case where membership doesn't exist
        });
    }

    @Override
    public void leaveRoom(Long userId, Long roomId) {
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByUserIdAndRoomId(userId, roomId);
        roomMembership.ifPresentOrElse(membership -> {
            this.contentServiceClient.decrementJoinedUsers(roomId);
            this.flagSubmissionService.deleteSubmissionsForUserAndRoom(userId, roomId);
            // Cases
            if (membership.getIsSaved()) { // if the room is Saved
                membership.setIsJoined(false); // we set is Joined to False
                membership.setChallengesCompleted(0);
                this.roomMembershipRepository.save(membership);
            } else { // if the room is not saved, we delete the room membership
                this.roomMembershipRepository.delete(membership);
            }
        }, () -> {
            throw new RoomMembershipNotFoundException("User has not joined this room"); // Handle case where membership doesn't exist
        });
    }

    @Override
    public void resetRoomProgress(Long userId, Long roomId) {

    }

    @Override
    public List<RoomDTO> getRoomCatalogForUser(Long userId) {
        List<RoomDTO> allRooms = contentServiceClient.getAllRooms();
        List<RoomDTO> joinedRooms = getJoinedRooms(userId);
        List<RoomDTO> savedRooms = getSavedRooms(userId);
        // Enrich each room with user-specific data
        return allRooms.stream()
                .map(room -> enrichRoomWithUserData(room, joinedRooms, savedRooms))
                .collect(Collectors.toList());
    }

    private RoomDTO enrichRoomWithUserData(RoomDTO room, List<RoomDTO> joinedRooms, List<RoomDTO> savedRooms) {
        // Check if the room is joined by the user
        joinedRooms.stream()
                .filter(joinedRoom -> joinedRoom.getId().equals(room.getId()))
                .findFirst() // terminal operation
                .ifPresent(joinedRoom -> {
                    room.setIsJoined(joinedRoom.getIsJoined());
                    room.setIsSaved(joinedRoom.getIsSaved());
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
        // Memberships for joined rooms -> extraction of the Ids of joined rooms
        List<RoomMembership> memberships = roomMembershipRepository.findByUserIdAndIsJoinedTrue(userId);
        List<Long> roomIds = memberships.stream().map(membership -> membership.getRoomId()).toList();
        // joined rooms via open feign client
        List<RoomDTO> joinedRooms = contentServiceClient.getRoomsByIds(roomIds); // this will return RoomDTOs
        // map
        Map<Long, RoomDTO> joinedRoomsMap = joinedRooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        // list of roomDTOs
        return memberships.stream().map(membership -> {
            RoomDTO room = joinedRoomsMap.get(membership.getRoomId()); // we get the room corresponding to the membership
            return roomMapper.toUserSpecificDTO(room, membership);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getSavedRooms(Long userId) {
        List<RoomMembership> memberships = roomMembershipRepository.findByUserIdAndIsSavedTrue(userId);
        List<Long> roomIds = memberships.stream().map(membership -> membership.getRoomId()).toList();
        List<RoomDTO> savedRooms = contentServiceClient.getRoomsByIds(roomIds);
        Map<Long, RoomDTO> savedRoomsMap = savedRooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        return memberships.stream().map(membership -> {
            RoomDTO room = savedRoomsMap.get(membership.getRoomId());
            return roomMapper.toUserSpecificDTO(room, membership);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getCompletedRooms(Long userId) {
        List<RoomMembership> allMemberships = roomMembershipRepository.findByUserId(userId); // we retrieve all memberships
        List<Long> roomIds = allMemberships.stream().map(membership -> membership.getRoomId()).toList();
        List<RoomDTO> rooms = contentServiceClient.getRoomsByIds(roomIds);
        Map<Long, RoomDTO> completedRoomsMap = rooms.stream().collect(Collectors.toMap(room -> room.getId(), room -> room));
        return allMemberships.stream()
                .filter(membership -> {
                    RoomDTO room = completedRoomsMap.get(membership.getRoomId()); // we extract the room related to the membership
                    if (room == null || room.getTotalChallenges() == 0) {return false;}
                    return membership.getChallengesCompleted() == room.getTotalChallenges();
                })
                .map(membership -> {
                    // After filtering, map the remaining (completed) rooms to the final DTO
                    RoomDTO room = completedRoomsMap.get(membership.getRoomId());
                    return roomMapper.toUserSpecificDTO(room, membership);
                })
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getJoinedRoom(Long userId, Long roomId) {
        // 1. Get the local membership data to confirm the user has joined this room.
        RoomMembership roomMembership = roomMembershipRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RoomMembershipNotFoundException("User has not joined this room"));
        // 2. Get the full room details, including the list of all its challenges, from the content-service.
        RoomDTO roomDTO = contentServiceClient.getRoomById(roomId); // this can propagate an exception that will be caught by our decoder
        List<ChallengeDTO> challengesInThisRoom = roomDTO.getChallenges();

        // 3. Ids of completed challenges for this user.
        Set<Long> allSolvedChallengeIdsForUser = flagSubmissionRepository.findCorrectChallengeIdsByUserId(userId);

        // 4. Mark the challenges in this specific room as completed if their ID is in the user's "solved" set.
        challengesInThisRoom.forEach(challenge -> {
            if (allSolvedChallengeIdsForUser.contains(challenge.getId())) {
                challenge.setCompleted(true);
            }
        });

        // 5. Use your mapper to enrich the RoomDTO with the user-specific membership data.
        // We already have the challenges list modified in place, so the mapper will just add isJoined, isSaved, etc.
        RoomDTO finalDto = roomMapper.toUserSpecificDTO(roomDTO, roomMembership);
        finalDto.setChallenges(challengesInThisRoom);

        return finalDto;
    }

    @Override
    public Map<String, Boolean> getRoomMembershipStatus(long userId, long roomId) {
        Map<String, Boolean> status = new HashMap<>();
        Optional<RoomMembership> membership = roomMembershipRepository.findByUserIdAndRoomId(userId, roomId);
        if (membership.isPresent()) {
            status.put("isJoined", membership.get().getIsJoined());
            status.put("isSaved", membership.get().getIsSaved());
        } else {
            status.put("isJoined", false);
            status.put("isSaved", false);
        }
        return status;
    }
}
