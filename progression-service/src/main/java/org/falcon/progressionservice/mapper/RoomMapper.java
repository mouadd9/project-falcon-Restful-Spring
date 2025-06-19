package org.falcon.progressionservice.mapper;

import org.falcon.progressionservice.client.dto.RoomDTO;
import org.falcon.progressionservice.entity.RoomMembership;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    @SuppressWarnings("null")
    public RoomDTO toUserSpecificDTO(RoomDTO room, RoomMembership roomMembership) {
        // Add null check and divide-by-zero protection
        if (roomMembership != null && room.getTotalChallenges() > 0) {
            room.setPercentageCompleted((roomMembership.getChallengesCompleted() * 100) / room.getTotalChallenges());
        } else {
            room.setPercentageCompleted(0); // Default to 0% if no challenges or no membership
        }
        room.setIsJoined(roomMembership.getIsJoined());
        room.setIsSaved(roomMembership.getIsSaved());
        return room;
    }

}
