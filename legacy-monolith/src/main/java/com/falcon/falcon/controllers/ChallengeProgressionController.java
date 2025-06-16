package com.falcon.falcon.controllers;

import com.falcon.falcon.facades.ChallengeProgressionFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// User interactions with challenges
@RestController
@RequestMapping("/api/users/{userId}/challenges")
public class ChallengeProgressionController {
    private final ChallengeProgressionFacade challengeProgressionFacade;
    public ChallengeProgressionController(ChallengeProgressionFacade challengeProgressionFacade) {
        this.challengeProgressionFacade = challengeProgressionFacade;
    }


    // claude read this
    // this endpoint will be used for flag submission
    // when a user submits a flag , we check if the flag is correct, if so
     // if a wrong flag was already submitted before and isCorrect is set to false then we set it to true.
     // if the flag submission table has no entry related to this submission we create a new entry and then set isCorrect to true
    // if the flag is not correct :
      // if the flag submission entry already exists (isCorrect: false) -> note that a user cannot submit flags for an already submitted challenges (with a submission of isCorrect: true)
        // so this means just the challenges with a submission of isCorrect: false or no submission are available for submission in the frontend.
      // if the flag submission entry doesnt exists we create it and put isCorrect as false
    @PostMapping("/{challengeId}/submit")
    public ResponseEntity<Map<String, Boolean>> submitFlag(
            @PathVariable Long userId,
            @PathVariable Long challengeId,
            @RequestBody Map<String, String> submission) {
        boolean isCorrect = challengeProgressionFacade.submitFlag(userId, challengeId, submission.get("flag"));
        return new ResponseEntity<>(Map.of("correct", isCorrect), HttpStatus.OK);
    } // we either return false or true in response.

}
