package org.falcon.contentservice.service;

import org.falcon.contentservice.dto.ChallengeWithSolutionDTO;

public interface ChallengeService {
    ChallengeWithSolutionDTO getChallenge(Long challengeId);
}
