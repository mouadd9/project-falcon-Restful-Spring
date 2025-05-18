package com.falcon.falcon.controllers;

import com.falcon.falcon.services.ChallengeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Pure challenge management
@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // Methods:
    // - getAllChallenges()
    // - getChallengeById(challengeId)
    // - createChallenge(challengeData)
    // - updateChallenge(challengeId, challengeData)
    // - deletechallenge(challengeId)
}
