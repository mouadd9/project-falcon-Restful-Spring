package com.falcon.falcon.dtos.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatisticsDTO {
    private long joinedRooms;
    private long completedRooms;
    private long activeInstances;
    private long dailyStreak;
}
