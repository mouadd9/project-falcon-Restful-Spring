package com.falcon.falcon.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.falcon.dtos.statistics.GlobalRoomStatsDTO;
import com.falcon.falcon.dtos.statistics.ProfileStatisticsDTO;
import com.falcon.falcon.services.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileStatisticsDTO> getProfileStatistics(@PathVariable Long userId) {
        // TODO: Add validation/error handling (e.g., user exists)
        return ResponseEntity.ok(statisticsService.getProfileStatistics(userId));
    }

    @GetMapping("/rooms/global/{userId}")
    public ResponseEntity<GlobalRoomStatsDTO> getGlobalRoomStatistics(@PathVariable Long userId) {
        // TODO: Add validation/error handling
        return ResponseEntity.ok(statisticsService.getGlobalRoomStatistics(userId));
    }    
}
