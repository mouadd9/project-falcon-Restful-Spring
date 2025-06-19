package org.falcon.progressionservice.service;

public interface FlagSubmissionService {
    void deleteSubmissionsForUserAndRoom(Long userId, Long roomId);
}
