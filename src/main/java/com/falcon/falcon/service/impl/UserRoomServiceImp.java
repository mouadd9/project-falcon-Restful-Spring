package com.falcon.falcon.service.impl;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.entity.Room;
import com.falcon.falcon.entity.RoomMembership;
import com.falcon.falcon.entity.User;
import com.falcon.falcon.exceptions.membershipExceptions.RoomMembershipNotFoundException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserNotFoundException;
import com.falcon.falcon.mapper.ChallengeMapper;
import com.falcon.falcon.mapper.RoomMapper;
import com.falcon.falcon.mapper.UserMapper;
import com.falcon.falcon.repository.RoleRepository;
import com.falcon.falcon.repository.RoomMembershipRepository;
import com.falcon.falcon.repository.RoomRepository;
import com.falcon.falcon.repository.UserRepository;
import com.falcon.falcon.service.RoomService;
import com.falcon.falcon.service.UserRoomService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Service responsible for managing user-room relationships and interactions.
 * Handles operations such as joining/saving rooms, retrieving user-specific room lists,
 * and managing room memberships. This service acts as an intermediary layer between
 * users and rooms, maintaining the state of user progress and preferences within rooms
 * through the RoomMembership entity. All operations are user-context aware, providing
 * personalized room data based on the user's interaction history.
 */
@Service
public class UserRoomServiceImp implements UserRoomService {
    private final ChallengeMapper challengeMapper;
    private RoomMapper roomMapper;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private RoomMembershipRepository roomMembershipRepository;
    private RoomService roomService;

    public UserRoomServiceImp(UserRepository userRepository, RoomRepository roomRepository, RoomMembershipRepository roomMembershipRepository, RoleRepository roleRepository, PasswordEncoder bCryptPasswordEncoder, UserMapper userMapper, RoomMapper roomMapper, ChallengeMapper challengeMapper, RoomService roomService) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMembershipRepository = roomMembershipRepository;
        this.challengeMapper = challengeMapper;
        this.roomMapper = roomMapper;
        this.roomService = roomService;
    }

    // getJoinedRooms(Long userId)
    /*
    Retrieves all rooms that a user has explicitly joined
 * 1. Fetches the user with their memberships and rooms using a custom repository query
 * 2. Filters memberships where isJoined = true
 * 3. Maps each membership to a RoomDTO with user-specific data (progress, status)
 * 4. Returns the list of rooms with their associated user membership data
 * Throws UserNotFoundException if user doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getJoinedRooms(Long userId) throws UserNotFoundException {
        // first we retrieve the user (here we will use a custom query to get the user with its memberships and rooms) to avoid the static nature of EAGER and LAZY loading
        User user = this.userRepository.findUserWithMembershipsAndRoomsById(userId).orElseThrow(()->new UserNotFoundException("user not found"));
        // now we need to return all rooms joined by this user
        return user.getMemberships().stream()
                .filter(rm -> rm.getIsJoined().equals(true))
                .map(rm -> roomMapper.toUserSpecificDTO(rm.getRoom(), rm))
                .collect(Collectors.toList());
    }

    // getSavedRooms(Long userId):
    /*
     * Retrieves all rooms that a user has bookmarked/saved.
     * 1. Fetches the user with their memberships and rooms
     * 2. Filters memberships where isSaved = true
     * 3. Maps each membership to a RoomDTO with user-specific data
     * 4. Returns the list of saved rooms with their membership status
     * Throws UserNotFoundException if user doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getSavedRooms(Long userId) throws UserNotFoundException {
        User user = this.userRepository.findUserWithMembershipsAndRoomsById(userId).orElseThrow(()->new UserNotFoundException("user not found"));
        return user.getMemberships().stream()
                .filter(rm -> rm.getIsSaved().equals(true))
                .map(rm -> roomMapper.toUserSpecificDTO(rm.getRoom(), rm))
                .collect(Collectors.toList());
    }

    // getCompletedRooms(Long userId):
    /*
     * Retrieves all rooms that a user has fully completed.
     * 1. Fetches user with memberships and rooms
     * 2. Filters memberships where completion rate = 100%
     * 3. Maps each membership to a RoomDTO with completion data
     * 4. Returns list of completed rooms with their stats
     * Throws UserNotFoundException if user doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getCompletedRooms(Long userId) throws UserNotFoundException {
        User user = this.userRepository.findUserWithMembershipsAndRoomsById(userId).orElseThrow(()->new UserNotFoundException("user not found"));
        return user.getMemberships().stream()
                .filter(m -> m.getChallengesCompleted()/m.getRoom().getTotalChallenges() == 1)
                .map(m -> roomMapper.toUserSpecificDTO(m.getRoom(), m))
                .collect(Collectors.toList());
    }

    // getJoinedRoom(Long userId, Long roomId):
    /*
     * Retrieves detailed information about a specific room the user has joined.
     * 1. Fetches user with memberships, rooms, and challenges
     * 2. Finds the specific room membership
     * 3. Maps room to RoomDTO including:
     *    - Room details (title, description)
     *    - Challenge list
     *    - Completion status
     * 4. Returns detailed room info with user-specific data
     * Throws UserNotFoundException if user not found
     * Throws RoomNotFoundException if room not found in user's memberships
     */
    @Override
    @Transactional(readOnly = true)
    public RoomDTO getJoinedRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException {
        // here we will fetch The User, and then we will search for the room in the memberships
        User user = this.userRepository.findUserWithMembershipsAndRoomsAndChallengesById(userId).orElseThrow(()->new UserNotFoundException("user not found"));
        // now we will search for the room in the memberships
        return user.getMemberships().stream()
                .filter(rm -> rm.getRoom().getId().equals(roomId))
                .map(rm -> {
                    Room room = rm.getRoom();
                    RoomDTO roomDTO = roomMapper.toUserSpecificDTO(room, rm);
                    roomDTO.setChallenges(room.getChallenges().stream()
                            .map(challenge -> challengeMapper.toChallengeDTO(challenge))
                            .collect(Collectors.toList()));
                    return roomDTO;
                })
                .findFirst()
                .orElseThrow(()->new RoomNotFoundException("room not found")); // this unwraps the Optional and throws the exception if not found
    }

    @Override
    @Transactional
    public void joinRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException {
        // step 1 : this returns an optional (this may or may not have a roomMembership)
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByRoomIdAndUserId(roomId, userId);
        // step 2 : check
        roomMembership.ifPresentOrElse(
                // if the room membership is present
                membership -> {
                    this.roomService.incrementJoinedUsers(roomId);
                    membership.setIsJoined(true); // we set isJoined to true
                    this.roomMembershipRepository.save(membership); // we persist it
                },
                // if the room membership is not present this means the room was never saved before, and an entry in the roomMembership table is not there
                () -> {
                    User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found")); // we first retrieve the user
                    Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("room not found")); // then the room
                    RoomMembership newRoomMembership = new RoomMembership(); // then we create a new Room Membership
                    // we then set the relationship
                    newRoomMembership.setRoom(room);
                    newRoomMembership.setUser(user);
                    // then we mark the user as joined
                    newRoomMembership.setIsSaved(false);
                    newRoomMembership.setIsJoined(true);
                    this.roomMembershipRepository.save(newRoomMembership); // then we save the room membership
                    // in the future we should from here signal out to the roomService that a new user has joined a room
                }
        );

    }

    @Override
    @Transactional
    public void saveRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException {
        // again here we have two cases
        // if the user has joined the room then we will just modify the existing relationship
        // else we will create a new roomMembership

        // step 1
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByRoomIdAndUserId(roomId, userId);
        // step 2
        roomMembership.ifPresentOrElse(
                membership -> {
                    membership.setIsSaved(true);
                    this.roomMembershipRepository.save(membership);
                },
                // if the room membership is not present this means that there is no entry in the roomMembership table
                () -> {
                    User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found")); // we first retrieve the user
                    Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("room not found")); // then the room
                    RoomMembership newRoomMembership = new RoomMembership(); // then we create a new Room Membership
                    // we then set the relationship
                    newRoomMembership.setRoom(room);
                    newRoomMembership.setUser(user);
                    // then we mark the user as joined
                    newRoomMembership.setIsJoined(false);
                    newRoomMembership.setIsSaved(true);
                    this.roomMembershipRepository.save(newRoomMembership); // then we save the room membership
                    // in the future we should from here signal out to the roomService that a new user has joined a room
                }
        );

    }

    @Override
    @Transactional
    public void unSaveRoom(Long userId, Long roomId) throws RoomMembershipNotFoundException {
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByRoomIdAndUserId(roomId, userId);
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
    @Transactional
    public void leaveRoom(Long userId, Long roomId) throws RoomMembershipNotFoundException {
        Optional<RoomMembership> roomMembership = this.roomMembershipRepository.findByRoomIdAndUserId(roomId, userId);
        roomMembership.ifPresentOrElse(membership -> { // most likely the room membership will exist (with isJoined set to true) , because teh room shown in the UI is Joined by that user
            // STEP 1 :
            this.roomService.decrementJoinedUsers(roomId); // this function will decrement the number of Joined Users and then broadcast the info via sockets to subscribers.
            // STEP 2: [FUTURE] Insert your FlagSubmission clearing logic HERE
            // This is where you'll add the call to clear flag submissions
            // STEP 3 : cases
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

    // TO DO
    // here in the future we will integrate a user-challenges service
    // responsible for the relationship between users and challenges that will reset challenges progress.


}
