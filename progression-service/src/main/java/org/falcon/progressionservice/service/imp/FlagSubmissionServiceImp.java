package org.falcon.progressionservice.service.imp;

import org.falcon.progressionservice.client.ContentServiceClient;
import org.falcon.progressionservice.client.dto.RoomDTO;
import org.falcon.progressionservice.repository.FlagSubmissionRepository;
import org.falcon.progressionservice.service.FlagSubmissionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FlagSubmissionServiceImp implements FlagSubmissionService {
    private final ContentServiceClient contentServiceClient;
    private final FlagSubmissionRepository flagSubmissionRepository;

    public FlagSubmissionServiceImp(
            ContentServiceClient contentServiceClient,
            FlagSubmissionRepository flagSubmissionRepository
    ) {
        this.contentServiceClient = contentServiceClient;
        this.flagSubmissionRepository = flagSubmissionRepository;
    }

    @Override
    public boolean processSubmission(Long userId, Long challengeId, String submittedFlag) {
        return false;
    }

    @Override
    public void deleteSubmissionsForUserAndRoom(Long userId, Long roomId) {
        // Get a room with all its details and challenges
        RoomDTO roomDetails = contentServiceClient.getRoomById(roomId);
        if (roomDetails == null || roomDetails.getChallenges() == null || roomDetails.getChallenges().isEmpty()) {
            // If the room has no challenges, there's nothing to delete.
            return;
        }
        // Extract just the IDs of the challenges that are in this room.
        List<Long> challengeIdsInRoom = roomDetails.getChallenges().stream()
                .map(challengeDTO -> challengeDTO.getId())
                .toList();
        Set<Long> challengeIds = new HashSet<>(challengeIdsInRoom);

        // Step 2: Now, call a new repository method to delete all submissions for this user
        // that match the challenge IDs we just fetched.
        flagSubmissionRepository.deleteByUserIdAndChallengeIdIn(userId, challengeIds);

    }

}
