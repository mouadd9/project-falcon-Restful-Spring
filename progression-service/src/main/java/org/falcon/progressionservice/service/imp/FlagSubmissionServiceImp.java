package org.falcon.progressionservice.service.imp;

import org.falcon.progressionservice.repository.FlagSubmissionRepository;
import org.falcon.progressionservice.repository.RoomMembershipRepository;
import org.falcon.progressionservice.service.FlagSubmissionService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FlagSubmissionServiceImp implements FlagSubmissionService {
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final RoomMembershipRepository roomMembershipRepository;

    public FlagSubmissionServiceImp(
            FlagSubmissionRepository flagSubmissionRepository,
            RoomMembershipRepository roomMembershipRepository) {
        this.flagSubmissionRepository = flagSubmissionRepository;
        this.roomMembershipRepository = roomMembershipRepository;
    }
    @Override
    public void deleteSubmissionsForUserAndRoom(Long userId, Long roomId) {
        // Get all challenge IDs from this room
        Set<Long> challengeIds = flagSubmissionRepository
                .findChallengeIdsByUserIdAndRoomId(userId, roomId);

        if (!challengeIds.isEmpty()) {
            // Delete all submissions for these challenges
            flagSubmissionRepository.deleteByUserIdAndChallengeIdIn(userId, challengeIds);

            // Reset the challenges completed count in room membership
            Optional<RoomMembership> membership = roomMembershipRepository
                    .findByRoomIdAndUserId(roomId, userId);

            membership.ifPresent(rm -> {
                rm.setChallengesCompleted(0);
                roomMembershipRepository.save(rm);
            });
        }
    }
}
