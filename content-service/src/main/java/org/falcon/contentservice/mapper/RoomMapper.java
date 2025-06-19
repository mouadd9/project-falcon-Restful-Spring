package org.falcon.contentservice.mapper;

import org.falcon.contentservice.dto.RoomDTO;
import org.falcon.contentservice.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDTO toDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .amiId(room.getAmiId())
                .title(room.getTitle())
                .description(room.getDescription())
                .complexity(room.getComplexity())
                .estimatedTime(room.getEstimatedTime())
                .imageURL(room.getImageURL())
                .totalChallenges(room.getTotalChallenges())
                .totalJoinedUsers(room.getTotalJoinedUsers())
                .createdAt(room.getCreatedAt())
                .isJoined(false)
                .isSaved(false)
                .percentageCompleted(0)
                .build();
    }

    public Room toEntity(RoomDTO roomDTO) {
        return Room.builder()
                .amiId(roomDTO.getAmiId())
                .title(roomDTO.getTitle())
                .description(roomDTO.getDescription())
                .complexity(roomDTO.getComplexity())
                .estimatedTime(roomDTO.getEstimatedTime())
                .imageURL(roomDTO.getImageURL())
                .totalChallenges(roomDTO.getTotalChallenges())
                .totalJoinedUsers(0)
                .build();
    }

}
