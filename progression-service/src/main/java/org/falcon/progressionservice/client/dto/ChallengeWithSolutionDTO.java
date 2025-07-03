package org.falcon.progressionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeWithSolutionDTO {
    private Long id;
    private String flag;
    private RoomInfoDTO room;
}
