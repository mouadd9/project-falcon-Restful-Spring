package com.falcon.falcon.dtos;

import com.falcon.falcon.enums.Complexity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    // room related info
      // when the user consults the catalog of rooms this shows up
    private Long id;
    private String amiId;
    private String title;
    private String description;
    private Complexity complexity; // the complexity
    private String imageURL; // the image of the room
    private int estimatedTime; // the estimated time to complete the room
    private int totalChallenges; // total challenges
    // when the user selects a room to see its details this adds up
    private List<ChallengeDTO> challenges; // List of challenges the user can see
    private int totalRunningInstances; // the Number of running instances (these two change over time via sockets)
    private int totalJoinedUsers; // the Number of joined rooms (these two change over time via sockets)
    // user related info
    private Boolean isSaved;
    private Boolean isJoined;
    private int percentageCompleted;
}
