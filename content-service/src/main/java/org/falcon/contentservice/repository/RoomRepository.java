package org.falcon.contentservice.repository;

import org.falcon.contentservice.entity.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @EntityGraph(attributePaths = {"challenges"})
    Optional<Room> findRoomWithChallengesById(Long id);
    boolean existsByAmiId(String amiId);
}
