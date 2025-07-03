package org.falcon.contentservice.web;

import org.falcon.contentservice.dto.ChallengeWithSolutionDTO;
import org.falcon.contentservice.service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content/challenges") // Or your existing base path
public class ChallengeController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * Provides detailed challenge information including the flag and room ID.
     * INTENDED FOR INTERNAL SERVICE USE ONLY.
     * @param id The ID of the challenge.
     * @return A DTO containing the challenge's flag and room information.
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ChallengeWithSolutionDTO> getChallengeWithSolution(@PathVariable Long id) {
        ChallengeWithSolutionDTO challenge = challengeService.getChallenge(id); // This should throw a NotFoundException if it doesn't exist
        return ResponseEntity.ok(challenge);
    }

}
