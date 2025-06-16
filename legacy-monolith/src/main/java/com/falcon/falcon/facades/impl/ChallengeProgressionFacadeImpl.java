package com.falcon.falcon.facades.impl;

import com.falcon.falcon.facades.ChallengeProgressionFacade;
import com.falcon.falcon.services.FlagSubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeProgressionFacadeImpl implements ChallengeProgressionFacade {

    private final FlagSubmissionService flagSubmissionService;

    public ChallengeProgressionFacadeImpl(FlagSubmissionService flagSubmissionService) {
        this.flagSubmissionService = flagSubmissionService;
    }

    @Override
    @Transactional
    public boolean submitFlag(Long userId, Long challengeId, String flag) {
        return flagSubmissionService.processSubmission(userId, challengeId, flag);
    }

    @Override
    @Transactional
    public void resetChallengeProgress(Long userId, Long roomId) {
        flagSubmissionService.deleteSubmissionsForUserAndRoom(userId, roomId);
    }

}
