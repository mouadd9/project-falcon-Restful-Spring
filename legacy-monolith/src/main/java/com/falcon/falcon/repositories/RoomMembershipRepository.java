package com.falcon.falcon.repositories;

import com.falcon.falcon.entities.RoomMembership;
import com.falcon.falcon.enums.Complexity;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<RoomMembership> findByRoomIdAndUserId(Long roomId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @EntityGraph(attributePaths = {"room", "user"})
    Optional<RoomMembership> findWithRoomAndUserByRoomIdAndUserId(Long roomId, Long userId);

    // Counts rooms a user has explicitly joined
    long countByUser_IdAndIsJoinedTrue(Long userId);

    // Counts rooms a user has completed (challengesCompleted == room.totalChallenges AND room.totalChallenges > 0)
    @Query("SELECT COUNT(rm) FROM RoomMembership rm WHERE rm.user.id = :userId AND rm.room.totalChallenges > 0 AND rm.challengesCompleted = rm.room.totalChallenges")
    long countCompletedRoomsForUser(@Param("userId") Long userId);

    // Counts completed rooms by user and complexity (challengesCompleted == room.totalChallenges AND room.totalChallenges > 0)
    @Query("SELECT COUNT(rm) FROM RoomMembership rm WHERE rm.user.id = :userId AND rm.room.complexity = :complexity AND rm.room.totalChallenges > 0 AND rm.challengesCompleted = rm.room.totalChallenges")
    long countCompletedRoomsByUserIdAndComplexity(@Param("userId") Long userId, @Param("complexity") Complexity complexity);
}
