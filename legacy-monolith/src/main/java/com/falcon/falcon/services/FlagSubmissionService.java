package com.falcon.falcon.services;

public interface FlagSubmissionService {

    /**
     * Process a flag submission
     *
     * @param userId ID of the user submitting the flag
     * @param challengeId ID of the challenge for which the flag is being submitted
     * @param submittedFlag The flag text being submitted
     * @return boolean indicating if the submission was correct
     */
    boolean processSubmission(Long userId, Long challengeId, String submittedFlag);

    /**
     * Deletes all flag submissions for a user in a specific room
     *
     * @param userId ID of the user
     * @param roomId ID of the room
     */
    void deleteSubmissionsForUserAndRoom(Long userId, Long roomId);
}
