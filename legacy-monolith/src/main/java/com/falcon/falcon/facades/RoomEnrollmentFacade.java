package com.falcon.falcon.facades;

import com.falcon.falcon.dtos.RoomDTO;

import java.util.List;
import java.util.Map;

/**
 * Facade for operations related to user enrollment in rooms.
 * Manages personalized room catalogs, joining, leaving, saving, and status checking for rooms.
 */
public interface RoomEnrollmentFacade {
    /**
     * Gets a personalized catalog of all rooms with user-specific enrollment information.
     *
     * @param userId The user's ID
     * @return List of rooms with personalized data (joined status, saved status, completion percentage)
     */
    List<RoomDTO> getRoomCatalogForUser(Long userId);

    /**
     * Retrieves a list of rooms that a user has joined.
     *
     * @param userId The user's ID
     * @return List of rooms the user has joined
     */
    List<RoomDTO> getJoinedRooms(Long userId);

    /**
     * Retrieves a list of rooms that a user has saved.
     *
     * @param userId The user's ID
     * @return List of rooms the user has saved
     */
    List<RoomDTO> getSavedRooms(Long userId);

    /**
     * Retrieves a list of rooms that a user has completed.
     *
     * @param userId The user's ID
     * @return List of rooms the user has completed
     */
    List<RoomDTO> getCompletedRooms(Long userId);

    /**
     * Retrieves detailed information about a specific joined room.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     * @return Detailed room information
     */
    RoomDTO getJoinedRoom(Long userId, Long roomId);

    /**
     * Joins a user to a room.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     */
    void joinRoom(Long userId, Long roomId);

    /**
     * Saves a room for a user.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     */
    void saveRoom(Long userId, Long roomId);

    /**
     * Removes a user from a room.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     */
    void leaveRoom(Long userId, Long roomId);

    /**
     * Unsaves a room for a user.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     */
    void unsaveRoom(Long userId, Long roomId);

    /**
     * Gets the current status of a user's relationship with a room.
     *
     * @param userId The user's ID
     * @param roomId The room's ID
     * @return A map containing status flags
     */
    Map<String, Boolean> getRoomMembershipStatus(Long userId, Long roomId);

    void resetRoomProgress(Long userId, Long roomId);

}
