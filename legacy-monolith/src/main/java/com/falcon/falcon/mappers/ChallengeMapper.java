package com.falcon.falcon.mappers;

import com.falcon.falcon.dtos.ChallengeDTO;
import com.falcon.falcon.entities.Challenge;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDTO toChallengeDTO(Challenge challenge) {
        return ChallengeDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .instructions(challenge.getInstructions())
                .isCompleted(false) // Default to false for user-agnostic view
                .build();
    }

    public Challenge toChallenge(ChallengeDTO challengeDTO) {
        return Challenge.builder()
                .name(challengeDTO.getName())
                .title(challengeDTO.getTitle())
                .description(challengeDTO.getDescription())
                .instructions(challengeDTO.getInstructions())
                .build();
    }
}
