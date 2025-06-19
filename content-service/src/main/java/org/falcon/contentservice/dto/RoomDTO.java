package org.falcon.contentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.falcon.contentservice.enums.Complexity;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String amiId;
    private String title;
    private String description;
    private Complexity complexity;
    private String imageURL;
    private int estimatedTime;
    private int totalChallenges;
    private LocalDateTime createdAt;
    private List<ChallengeDTO> challenges;
    private int totalJoinedUsers;
    private Boolean isSaved;
    private Boolean isJoined;
    private int percentageCompleted;
}
