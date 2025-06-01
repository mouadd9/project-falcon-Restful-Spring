package com.falcon.falcon.repositories;

import com.falcon.falcon.entities.FlagSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FlagSubmissionRepository extends JpaRepository<FlagSubmission, Long> {
    /**
     * Finds the IDs of challenges that a specific user has successfully completed in a specific room.
     *
     * This method uses a JPQL query to:
     * 1. Join FlagSubmission entities with their associated Challenge entities
     * 2. Filter for a specific user, room, and only correct submissions
     * 3. Project only the challenge IDs for efficient data transfer
     *
     * The result is a Set of challenge IDs that will be used to mark challenges
     * as completed in the user interface.
     *
     * Following JPA naming conventions, this is a custom query method with
     * explicit parameter names for clarity.
     *
     * SELECT c.id
     * FROM flag_submission fs
     * JOIN challenge c ON fs.challenge_id = c.id
     * WHERE fs.user_id = ?
     *   AND c.room_id = ?
     *   AND fs.is_correct = true;
     *
     * @param userId The ID of the user whose completed challenges we want to find
     * @param roomId The ID of the room containing the challenges
     * @return A Set of challenge IDs that the user has successfully completed
     */
    @Query("SELECT fs.challenge.id FROM FlagSubmission fs WHERE fs.user.id = :userId AND fs.challenge.room.id = :roomId AND fs.isCorrect = true")
    Set<Long> findCompletedChallengeIdsByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);

    /**
     * Finds a flag submission by user ID and challenge ID
     */
    @Query("SELECT fs FROM FlagSubmission fs WHERE fs.user.id = :userId AND fs.challenge.id = :challengeId")
    Optional<FlagSubmission> findByUserIdAndChallengeId(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    /**
     * Finds all challenge IDs from a room that a user has submitted flags for
     */
    @Query("SELECT fs.challenge.id FROM FlagSubmission fs WHERE fs.user.id = :userId AND fs.challenge.room.id = :roomId")
    Set<Long> findChallengeIdsByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);

    /**
     * Deletes flag submissions by user ID and a set of challenge IDs
     */
    @Modifying
    @Query("DELETE FROM FlagSubmission fs WHERE fs.user.id = :userId AND fs.challenge.id IN :challengeIds")
    void deleteByUserIdAndChallengeIdIn(@Param("userId") Long userId, @Param("challengeIds") Set<Long> challengeIds);



    /**
     * Checks if a user has at least one correct flag submission on a specific calendar date.
     * Note: The exact SQL function for date extraction (e.g., DATE(), TRUNC()) might vary
     * depending on your database (H2, MySQL, PostgreSQL, Oracle, etc.).
     * This example uses a common approach. Adjust if necessary for your specific DB.
     */
    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN TRUE ELSE FALSE END " +
           "FROM FlagSubmission fs " +
           "WHERE fs.user.id = :userId " +
           "AND fs.isCorrect = true " +
           "AND FUNCTION('DATE', fs.submissionDate) = :targetDate") // Assumes submissionDate field
    boolean hasCorrectSubmissionOnDate(@Param("userId") Long userId, @Param("targetDate") java.sql.Date targetDate);

    // Alternative if your submissionDate is a Timestamp and you want to check a date range for a given LocalDate:
    // boolean existsByUser_IdAndIsCorrectTrueAndSubmissionDateBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
