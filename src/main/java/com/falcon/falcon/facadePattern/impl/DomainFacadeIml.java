package com.falcon.falcon.facadePattern.impl;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.facadePattern.DomainFacade;
import com.falcon.falcon.service.RoomService;
import com.falcon.falcon.service.UserRoomService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * The aggregation layer is a design pattern that combines data and functionality from several sources or services into a single
 * higher-level API. In our project, the DomainFacade serves as the aggregation layer by orchestrating calls to RoomService,
 * UserService, and UserRoomService. This layer consolidates base room data and user-specific room details, ensuring that the
 * client receives all the necessary information in one response. It separates concerns by keeping lower-level services focused on
 * their specific tasks while the aggregation layer handles the integration and transformation of that data.
 */
// ready for tests !!
@Component
public class DomainFacadeIml implements DomainFacade {
    private final RoomService roomService;
    private final UserRoomService userRoomService;

    public DomainFacadeIml(RoomService roomService, UserRoomService userRoomService) {
        this.roomService = roomService;
        this.userRoomService = userRoomService;
    }

    // this method needs three things
      // - all rooms with no information about users
      // - joined rooms by a user, these rooms should contain information about the membership
      // - saved rooms

    // and it should return a list of rooms that contain information about the membership
      // - isJoined / joinedAt / isSaved / completedAt / percentageCompleted

    @Override
    @Transactional
    public List<RoomDTO> getRoomCatalogForUser(Long userId) {
        // step 1 : Get all base room information using roomManager/roomService this service provides us with information regarding rooms NOT USERS
        List<RoomDTO> allRooms = roomService.getAllRooms();
        // these rooms are not joined or saved by default and their achievement rate is by default 0 until we check the user's joined/saved rooms
        // step 2 : Get rooms user has joined or saved  using userManager/userService this service provides us with information regarding users NOT ROOMS (like for example rooms a user has joined !!!)
        List<RoomDTO> joinedRooms = userRoomService.getJoinedRooms(userId); // this method, retrieves a user and its memberships, for each membership it gets the room associated with that membership. it returns a list of roomDTOs
        List<RoomDTO> savedRooms = userRoomService.getSavedRooms(userId);

        // step 3 : Use streams
        return allRooms.stream() //
                .map(room -> enrichRoomWithUserData(room, joinedRooms, savedRooms))
                .collect(Collectors.toList());
    }

    private RoomDTO enrichRoomWithUserData(RoomDTO room, List<RoomDTO> joinedRooms, List<RoomDTO> savedRooms) {
        // we check if the room has joined by the user.
        joinedRooms.stream()
                .filter(joinedRoom -> joinedRoom.getId().equals(room.getId()))
                .findFirst() // terminal operation
                .ifPresent(joinedRoom -> { // if the
                    room.setIsJoined(joinedRoom.getIsJoined());
                    room.setIsSaved(joinedRoom.getIsSaved()); // this will set is saved to false if the user didnt save the room
                    room.setPercentageCompleted(joinedRoom.getPercentageCompleted());
                });

        // we check if the user has saved the room
        savedRooms.stream()
                .filter(r -> r.getId().equals(room.getId()))
                .findFirst()
                .ifPresent(savedRoom -> room.setIsSaved(true));

        return room;
    }
}


// Streams process sequences of data
// Streams are not data structures, they are a way to process data in a functional style
// data sources are mainly collections with finite elements.
// Streams are not stored in memory, they are computed on demand !
// To perform a sequence of operations over the elements of the data source and aggregate their results, we need three parts: the source, intermediate operation(s) and a terminal operation.
// in order to process sequences of data sourced from collections we define a pipeline of operations on a collection
// the pipeline is a sequence of operations that are applied to the elements of the collection
// Streams are Synchronous. When you invoke a terminal operation, the stream processes all data immediately and blocks until completion.
// We can only use one terminal operation per stream
// intermediate operations which reduce the size of the stream should be placed before operations which are applying to each element. So we need to keep methods such as skip(), filter(), and distinct() at the top of our stream pipeline.