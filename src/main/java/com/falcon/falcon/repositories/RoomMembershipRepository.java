package com.falcon.falcon.repositories;

import com.falcon.falcon.entities.RoomMembership;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<RoomMembership> findByRoomIdAndUserId(Long roomId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @EntityGraph(attributePaths = {"room", "user"})
    Optional<RoomMembership> findWithRoomAndUserByRoomIdAndUserId(Long roomId, Long userId);
}
