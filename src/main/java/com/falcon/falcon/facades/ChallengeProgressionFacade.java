package com.falcon.falcon.facades;

public interface ChallengeProgressionFacade {
    /**
     * Processes a user's flag submission for a challenge
     *
     * @param userId ID of the user submitting the flag
     * @param challengeId ID of the challenge for which the flag is being submitted
     * @param flag The flag text being submitted
     * @return boolean indicating if the submission was correct
     */
    boolean submitFlag(Long userId, Long challengeId, String flag);

    /**
     * Reset all flag submissions for a user in a specific room
     *
     * @param userId ID of the user
     * @param roomId ID of the room
     */
    void resetChallengeProgress(Long userId, Long roomId);
}
