package org.falcon.contentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class ChallengeDTO {
    private Long id;
    private String name;
    private String title;
    private String description;
    private String instructions;
    private boolean isCompleted;
}
