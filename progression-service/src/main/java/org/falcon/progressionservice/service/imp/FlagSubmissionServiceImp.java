package org.falcon.progressionservice.service.imp;

import org.falcon.progressionservice.client.ContentServiceClient;
import org.falcon.progressionservice.client.dto.ChallengeDTO;
import org.falcon.progressionservice.client.dto.ChallengeWithSolutionDTO;
import org.falcon.progressionservice.client.dto.RoomDTO;
import org.falcon.progressionservice.entity.FlagSubmission;
import org.falcon.progressionservice.entity.RoomMembership;
import org.falcon.progressionservice.repository.FlagSubmissionRepository;
import org.falcon.progressionservice.repository.RoomMembershipRepository;
import org.falcon.progressionservice.service.FlagSubmissionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlagSubmissionServiceImp implements FlagSubmissionService {
    private final ContentServiceClient contentServiceClient;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final RoomMembershipRepository roomMembershipRepository;

    public FlagSubmissionServiceImp(ContentServiceClient contentServiceClient, FlagSubmissionRepository flagSubmissionRepository, RoomMembershipRepository roomMembershipRepository) {
        this.contentServiceClient = contentServiceClient;
        this.flagSubmissionRepository = flagSubmissionRepository;
        this.roomMembershipRepository = roomMembershipRepository;
    }

    // to do
    // create the method to get a challenge by ID (the challenge object should contain its room)
    // create a new DTO that has a challenge and its Room the flag DTO by adding a flag property for a challenge so that we can  find the flag for a challenge
    // manage the challenge not found exception
    @Override
    public boolean processSubmission(Long userId, Long challengeId, String submittedFlag) {
        // Step 1: Call the NEW endpoint to get the challenge with its flag and room.
        // This replaces your old call
        ChallengeWithSolutionDTO challenge = contentServiceClient.getChallengeWithSolutionById(challengeId); // Throws exception if not found

        // Check if user has already submitted this challenge
        Optional<FlagSubmission> existingSubmission = flagSubmissionRepository
                .findByUserIdAndChallengeId(userId, challengeId);

        // Step 3: Check if the submitted flag is correct using the new DTO.
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
                submission.setSubmissionDate(new Date());

                // Update challenge completion count in room membership
                updateChallengeCompletionCount(userId, challenge.getRoom().getId());
            }

            flagSubmissionRepository.save(submission);
        } else { // if the user never tried to submit a flag before
            // Create new submission
            FlagSubmission newSubmission = new FlagSubmission();
            newSubmission.setUserId(userId);
            newSubmission.setChallengeId(challengeId);
            newSubmission.setSubmittedFlag(submittedFlag);
            newSubmission.setIsCorrect(isCorrect);
            newSubmission.setSubmissionDate(new Date());

            flagSubmissionRepository.save(newSubmission);

            if (isCorrect) {
                // Update challenge completion count in room membership
                updateChallengeCompletionCount(userId, challenge.getRoom().getId());
            }
        }

        return isCorrect;
    }

    private void updateChallengeCompletionCount(Long userId, Long roomId) {
        // Find room membership
        Optional<RoomMembership> membership = roomMembershipRepository
                .findByUserIdAndRoomId(userId, roomId);

        membership.ifPresent(rm -> {
            // 3. Call your new method to get the set of completed challenge IDs.
            // This replaces the old direct repository call.
            Set<Long> completedChallengeIds = getCompletedChallengesForUserInRoom(userId, roomId);

            // 4. Get the size of the resulting set.
            long completedChallengesCount = completedChallengeIds.size();

            // Update the count
            rm.setChallengesCompleted((int) completedChallengesCount);
            roomMembershipRepository.save(rm);
        });
    }
    @Override
    public void deleteSubmissionsForUserAndRoom(Long userId, Long roomId) {
        // Get a room with all its details and challenges
        RoomDTO roomDetails = contentServiceClient.getRoomById(roomId); // this may generate an exception
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

    private Set<Long> getCompletedChallengesForUserInRoom(Long userId, Long roomId) {

        // 1. Fetch all challenges belonging to the specified room from the content service.
        // This is your existing code.
        RoomDTO roomDTO = contentServiceClient.getRoomById(roomId);
        List<ChallengeDTO> challengesInThisRoom = roomDTO.getChallenges();

        // If there are no challenges in the room, we can stop here.
        if (challengesInThisRoom == null || challengesInThisRoom.isEmpty()) {
            return Collections.emptySet();
        }

        // Extract just the IDs from the list of challenges in the room.
        Set<Long> challengeIdsInRoom = challengesInThisRoom.stream()
                .map(ChallengeDTO::getId)
                .collect(Collectors.toSet());

        // 2. Get the IDs of ALL challenges this user has EVER solved.
        // This is also your existing code, assuming you have the corresponding repository method.
        Set<Long> allSolvedChallengeIdsForUser = flagSubmissionRepository.findCorrectChallengeIdsByUserId(userId);

        // 3. Find the intersection of the two sets.
        // This is the key step: keep only the elements from `challengeIdsInRoom` that are also present
        // in the `allSolvedChallengeIdsForUser` set.
        challengeIdsInRoom.retainAll(allSolvedChallengeIdsForUser);

        return challengeIdsInRoom;
    }

}
