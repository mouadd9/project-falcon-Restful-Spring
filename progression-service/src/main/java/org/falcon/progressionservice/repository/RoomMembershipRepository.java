package org.falcon.progressionservice.repository;

import jakarta.persistence.LockModeType;
import org.falcon.progressionservice.entity.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    // find all room memberships for joined rooms
    List<RoomMembership> findByUserIdAndIsJoinedTrue(Long userId);
    // find all room memberships for saved rooms
    List<RoomMembership> findByUserIdAndIsSavedTrue(Long userId);
    // find all room memberships affiliated with a user
    List<RoomMembership> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<RoomMembership> findByUserIdAndRoomId(Long userId, Long roomId);
}
