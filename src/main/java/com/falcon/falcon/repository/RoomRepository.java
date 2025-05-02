package com.falcon.falcon.repository;

import com.falcon.falcon.entity.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @EntityGraph(attributePaths = {"challenges"})
    Optional<Room> findRoomWithChallengesById(Long id); // this fetches a room and all its challenges and returns an Optional with the room object with a list of fetched challenges
    boolean existsByAmiId(String amiId); // this checks if a room with the same amiId already exists
}
