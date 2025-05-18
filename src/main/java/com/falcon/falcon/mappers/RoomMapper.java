package com.falcon.falcon.mappers;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.entities.Room;
import com.falcon.falcon.entities.RoomMembership;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDTO toUserSpecificDTO(Room room, RoomMembership roomMembership) {
        RoomDTO dto = toDTO(room);
        // Add null check and divide-by-zero protection
        if (roomMembership != null && room.getTotalChallenges() > 0) {
            dto.setPercentageCompleted((roomMembership.getChallengesCompleted() * 100) / room.getTotalChallenges());
        } else {
            dto.setPercentageCompleted(0); // Default to 0% if no challenges or no membership
        }
        dto.setIsJoined(roomMembership.getIsJoined());
        dto.setIsSaved(roomMembership.getIsSaved());
        return dto;
    }

    public RoomDTO toDTO(Room room) {
        return RoomDTO.builder()
                // over all information
                .id(room.getId())
                .amiId(room.getAmiId())
                .title(room.getTitle())
                .description(room.getDescription())
                .complexity(room.getComplexity())
                .estimatedTime(room.getEstimatedTime())
                .imageURL(room.getImageURL())
                .totalChallenges(room.getTotalChallenges())
                .totalJoinedUsers(room.getTotalJoinedUsers())
                .totalRunningInstances(room.getTotalRunningInstances())
                .isJoined(false) // this is set to false because we still dont know the user we are retrieveving the room for
                .isSaved(false) // same thing
                .percentageCompleted(0) // same thing
                .build();
    }

    // now if we are creating a new room we need a mapper that turns a DTO to an entity
    public Room toEntity(RoomDTO roomDTO) {
        return Room.builder()
                .amiId(roomDTO.getAmiId())
                .title(roomDTO.getTitle())
                .description(roomDTO.getDescription())
                .complexity(roomDTO.getComplexity())
                .estimatedTime(roomDTO.getEstimatedTime())
                .imageURL(roomDTO.getImageURL())
                .totalChallenges(roomDTO.getTotalChallenges())
                .totalJoinedUsers(0) // this is set to 0 because we are creating a new room
                .totalRunningInstances(0) // this is set to 0 because we are creating a new room
                .build();
    }

}
