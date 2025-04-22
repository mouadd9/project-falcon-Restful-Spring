package com.falcon.falcon.service;

import com.falcon.falcon.dto.UserDTO;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;

// tracks user's relationship with rooms via memberships
public interface UserService {
    void validateEmailNotExists(String email) throws UserAlreadyExistsException;
    UserDTO createUser(UserDTO user) throws UserAlreadyExistsException;

   /* void leaveRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException; // if the room is saved we will only reset the progress and unjoin the user
    void unsaveRoom(Long userId, Long roomId) throws UserNotFoundException, RoomNotFoundException; // if the room is saved and joined we will un save it, if its saved and unjoined we will delete the room membership
  */
}

/**
 * This service handles user-related operations, including membership management.
 *
 * The joinRoom method is placed here because:
 * 1. The User entity is configured with cascade=CascadeType.ALL on its memberships collection,
 *    meaning it's responsible for persisting RoomMembership entities
 * 2. From a domain perspective, users initiate the action of joining rooms
 * 3. Transaction boundaries are cleaner when a single service manages the entire operation
 *
 * By following these principles, we maintain a clear separation of concerns where
 * UserService manages user-driven actions and the lifecycle of entities owned by User.
 */