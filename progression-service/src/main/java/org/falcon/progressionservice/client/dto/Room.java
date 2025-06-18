package org.falcon.progressionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.falcon.progressionservice.dto.RoomDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class Room {
    private Long id;
    private String amiId;
    private String title;
    private String description;

    private RoomDTO.Complexity complexity;
    private int totalChallenges;
    private String imageURL;
    private int estimatedTime;
    private LocalDateTime createdAt;
    private int totalJoinedUsers;
    private Collection<Challenge> challenges = new ArrayList<>();
}
