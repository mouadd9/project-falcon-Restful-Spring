package org.falcon.progressionservice.service;

public interface FlagSubmissionService {
    boolean processSubmission(Long userId, Long challengeId, String submittedFlag);
    void deleteSubmissionsForUserAndRoom(Long userId, Long roomId);
}
