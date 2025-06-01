package com.falcon.falcon.dtos.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRoomStatsDTO {
    private StatDetail totalRooms;
    private StatDetail easyRooms;
    private StatDetail mediumRooms;
    private StatDetail hardRooms;
}
