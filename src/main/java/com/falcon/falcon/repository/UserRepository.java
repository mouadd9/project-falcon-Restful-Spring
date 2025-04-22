package com.falcon.falcon.repository;

import com.falcon.falcon.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // we will use derived query method provided by Spring Data JPA
    // in this example we want to check if a user with a given email exists
    // the naming is not arbitrary, it must follow certain rules
    Optional<User> findByEmail(String email); // this returns an Optional that may or may not have a user
    Optional<User> findByUsername(String username);

    // Basic graph - loads only memberships
    @EntityGraph(attributePaths = {"memberships"})
    Optional<User> findUserWithBasicMembershipsById(Long id);

    @EntityGraph(attributePaths = {"memberships", "memberships.room"})
    Optional<User> findUserWithMembershipsAndRoomsById(Long id);

    @EntityGraph(attributePaths = {"memberships", "memberships.room", "memberships.room.challenges"})
    Optional<User> findUserWithMembershipsAndRoomsAndChallengesById(Long id); // this fetches a user and all its memberships and rooms and challenges
}
