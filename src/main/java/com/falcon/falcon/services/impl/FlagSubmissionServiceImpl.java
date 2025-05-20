package com.falcon.falcon.services.impl;

import com.falcon.falcon.entities.Challenge;
import com.falcon.falcon.entities.FlagSubmission;
import com.falcon.falcon.entities.RoomMembership;
import com.falcon.falcon.entities.User;
import com.falcon.falcon.exceptions.challengeExceptions.ChallengeNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserNotFoundException;
import com.falcon.falcon.repositories.ChallengeRepository;
import com.falcon.falcon.repositories.FlagSubmissionRepository;
import com.falcon.falcon.repositories.RoomMembershipRepository;
import com.falcon.falcon.repositories.UserRepository;
import com.falcon.falcon.services.FlagSubmissionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class FlagSubmissionServiceImpl implements FlagSubmissionService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final RoomMembershipRepository roomMembershipRepository;

    public FlagSubmissionServiceImpl(ChallengeRepository challengeRepository,
                                     UserRepository userRepository,
                                     FlagSubmissionRepository flagSubmissionRepository,
                                     RoomMembershipRepository roomMembershipRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.flagSubmissionRepository = flagSubmissionRepository;
        this.roomMembershipRepository = roomMembershipRepository;
    }

    @Override
    public boolean processSubmission(Long userId, Long challengeId, String submittedFlag) {
        // Validate user and challenge exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeNotFoundException("Challenge not found with ID: " + challengeId));

        // Check if user has already correctly submitted this challenge
        Optional<FlagSubmission> existingSubmission = flagSubmissionRepository
                .findByUserIdAndChallengeId(userId, challengeId);

        // Check if the submitted flag is correct
        boolean isCorrect = challenge.getFlag().equals(submittedFlag);

        if (existingSubmission.isPresent()) {
            // Update existing submission
            FlagSubmission submission = existingSubmission.get();

            // If already correct, return true without changing (this case will likely never happen)
            if (submission.getIsCorrect()) {
                return true;
            }

            // if the user has already submitted a false flag

            // we update the submission
            submission.setSubmittedFlag(submittedFlag);

            if (isCorrect) {
                // Update from incorrect to correct
                submission.setIsCorrect(true);
                submission.setSumbissionDate(new Date());

                // Update challenge completion count in room membership
                updateChallengeCompletionCount(userId, challenge.getRoom().getId());
            }

            flagSubmissionRepository.save(submission);
        } else { // if the user never tried to submit a flag before
            // Create new submission
            FlagSubmission newSubmission = new FlagSubmission();
            newSubmission.setUser(user);
            newSubmission.setChallenge(challenge);
            newSubmission.setSubmittedFlag(submittedFlag);
            newSubmission.setIsCorrect(isCorrect);
            newSubmission.setSumbissionDate(new Date());

            flagSubmissionRepository.save(newSubmission);

            if (isCorrect) {
                // Update challenge completion count in room membership
                updateChallengeCompletionCount(userId, challenge.getRoom().getId());
            }
        }

        return isCorrect;
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

    private void updateChallengeCompletionCount(Long userId, Long roomId) {
        // Find room membership
        Optional<RoomMembership> membership = roomMembershipRepository
                .findByRoomIdAndUserId(roomId, userId);

        membership.ifPresent(rm -> {
            // Count total completed challenges
            long completedChallenges = flagSubmissionRepository
                    .findCompletedChallengeIdsByUserIdAndRoomId(userId, roomId)
                    .size();

            // Update the count
            rm.setChallengesCompleted((int) completedChallenges);
            roomMembershipRepository.save(rm);
        });
    }
}
