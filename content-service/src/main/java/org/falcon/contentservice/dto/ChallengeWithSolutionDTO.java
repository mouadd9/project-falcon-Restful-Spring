package org.falcon.contentservice.dto;

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
    private String flag; // The correct flag
    private RoomInfoDTO room; // The associated room info
}
