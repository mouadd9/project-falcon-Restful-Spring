package com.falcon.falcon.repositories;

import com.falcon.falcon.entities.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    /**
     * Finds an instance by room ID and user ID.
     * Assumes one instance per user per room.
     */
    Optional<Instance> findByRoomIdAndUserId(Long roomId, Long userId);

    /**
     * Alternative: find the most recent instance for a user in a room
     * if multiple instances are allowed
     */
    @Query("SELECT i FROM Instance i WHERE i.room.id = :roomId AND i.user.id = :userId ORDER BY i.launchDate DESC")
    Optional<Instance> findLatestByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
