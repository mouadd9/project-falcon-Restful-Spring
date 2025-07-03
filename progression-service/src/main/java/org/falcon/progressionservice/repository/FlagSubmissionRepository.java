package org.falcon.progressionservice.repository;

import org.falcon.progressionservice.entity.FlagSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FlagSubmissionRepository extends JpaRepository<FlagSubmission, Long> {
    /**
     * Finds all challenge IDs that a user has correctly submitted flags for,
     * across all rooms. This query only uses local data.
     */
    @Query("SELECT fs.challengeId FROM FlagSubmission fs WHERE fs.userId = :userId AND fs.isCorrect = true")
    Set<Long> findCorrectChallengeIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM FlagSubmission fs WHERE fs.userId = :userId AND fs.challengeId IN :challengeIds")
    void deleteByUserIdAndChallengeIdIn(@Param("userId") Long userId, @Param("challengeIds") Set<Long> challengeIds);

    Optional<FlagSubmission> findByUserIdAndChallengeId(Long userId, Long challengeId);


}
