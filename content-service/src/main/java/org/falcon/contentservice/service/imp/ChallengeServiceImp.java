package org.falcon.contentservice.service.imp;

import org.falcon.contentservice.dto.ChallengeWithSolutionDTO;
import org.falcon.contentservice.dto.RoomInfoDTO;
import org.falcon.contentservice.entity.Challenge;
import org.falcon.contentservice.exception.ChallengeNotFoundException;
import org.falcon.contentservice.repository.ChallengeRepository;
import org.falcon.contentservice.service.ChallengeService;
import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImp implements ChallengeService {
    private final ChallengeRepository challengeRepository;

    public ChallengeServiceImp(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public ChallengeWithSolutionDTO getChallenge(Long challengeId) {
        // 1. Retrieve the Challenge entity from the database or throw an exception
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeNotFoundException("Challenge not found with ID: " + challengeId));

        // 2. Create the nested DTO for the room information
        RoomInfoDTO roomInfo = new RoomInfoDTO(challenge.getRoom().getId());

        // 3. Build and return the main DTO
        return ChallengeWithSolutionDTO.builder()
                .id(challenge.getId())
                .flag(challenge.getFlag())
                .room(roomInfo)
                .build();
    }
}
