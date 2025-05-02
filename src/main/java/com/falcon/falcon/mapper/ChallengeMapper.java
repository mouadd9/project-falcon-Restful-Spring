package com.falcon.falcon.mapper;

import com.falcon.falcon.dto.ChallengeDTO;
import com.falcon.falcon.entity.Challenge;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeDTO toChallengeDTO(Challenge challenge) {
        return ChallengeDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .flag(null)
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .build();
    }

    public Challenge toChallenge(ChallengeDTO challengeDTO) {
        return Challenge.builder()
                .name(challengeDTO.getName())
                .title(challengeDTO.getTitle())
                .description(challengeDTO.getDescription())
                .flag(challengeDTO.getFlag())
                .build();
    }
}
