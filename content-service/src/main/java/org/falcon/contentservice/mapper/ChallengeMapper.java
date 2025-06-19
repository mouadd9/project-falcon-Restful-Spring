package org.falcon.contentservice.mapper;

import org.falcon.contentservice.dto.ChallengeDTO;
import org.falcon.contentservice.entity.Challenge;
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
                .isCompleted(false)
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
