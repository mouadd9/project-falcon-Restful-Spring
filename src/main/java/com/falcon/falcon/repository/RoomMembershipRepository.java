package com.falcon.falcon.repository;

import com.falcon.falcon.entity.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    Optional<RoomMembership> findByRoomIdAndUserId(Long roomId, Long userId);
}
