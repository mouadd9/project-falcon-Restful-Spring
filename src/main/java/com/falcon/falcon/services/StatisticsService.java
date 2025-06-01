package com.falcon.falcon.services;

import com.falcon.falcon.dtos.statistics.GlobalRoomStatsDTO;
import com.falcon.falcon.dtos.statistics.ProfileStatisticsDTO;

public interface StatisticsService {
    ProfileStatisticsDTO getProfileStatistics(Long userId);
    GlobalRoomStatsDTO getGlobalRoomStatistics(Long userId);
}
