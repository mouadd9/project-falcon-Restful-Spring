package com.falcon.falcon.services.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.falcon.falcon.dtos.statistics.GlobalRoomStatsDTO;
import com.falcon.falcon.dtos.statistics.ProfileStatisticsDTO;
import com.falcon.falcon.dtos.statistics.StatDetail;
import com.falcon.falcon.enums.Complexity;
import com.falcon.falcon.enums.InstanceStateEnum;
import com.falcon.falcon.repositories.FlagSubmissionRepository;
import com.falcon.falcon.repositories.InstanceRepository;
import com.falcon.falcon.repositories.RoomMembershipRepository;
import com.falcon.falcon.repositories.RoomRepository;
import com.falcon.falcon.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final RoomRepository roomRepository;
    private final InstanceRepository instanceRepository; 
    private final RoomMembershipRepository roomMembershipRepository;
    private final FlagSubmissionRepository flagSubmissionRepository; // Add this

    public StatisticsServiceImpl(InstanceRepository instanceRepository, RoomRepository roomRepository, RoomMembershipRepository roomMembershipRepository, FlagSubmissionRepository flagSubmissionRepository) {
        this.roomRepository = roomRepository;
        this.roomMembershipRepository = roomMembershipRepository;
        this.flagSubmissionRepository = flagSubmissionRepository;
        this.instanceRepository = instanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileStatisticsDTO getProfileStatistics(Long userId) {

        ProfileStatisticsDTO stats = ProfileStatisticsDTO.builder()
                .joinedRooms(roomMembershipRepository.countByUser_IdAndIsJoinedTrue(userId))
                .completedRooms(roomMembershipRepository.countCompletedRoomsForUser(userId))
                .activeInstances(instanceRepository.countByUser_IdAndInstanceState(userId, InstanceStateEnum.RUNNING))
                .dailyStreak(calculateDailyStreak(userId))
                .build();
        
        return stats;
    }

    private long calculateDailyStreak(Long userId) {
        // Use server's current date. For testability, consider injecting a Clock.
        LocalDate today = LocalDate.now(ZoneId.systemDefault()); // Or simply LocalDate.now()
        long currentStreak = 0;

        // 1. Check if there's a correct submission today
        Date todaySqlDate = Date.valueOf(today);
        if (!flagSubmissionRepository.hasCorrectSubmissionOnDate(userId, todaySqlDate)) {
            return 0; // No submission today, streak is 0
        }

        currentStreak = 1; // At least 1 for today's submission

        // 2. Iterate backwards from yesterday
        LocalDate dayToCheck = today.minusDays(1);
        while (true) {
            Date dayToCheckSqlDate = Date.valueOf(dayToCheck);
            if (flagSubmissionRepository.hasCorrectSubmissionOnDate(userId, dayToCheckSqlDate)) {
                currentStreak++;
                dayToCheck = dayToCheck.minusDays(1); // Move to the day before
            } else {
                break; // Streak broken
            }
        }
        return currentStreak;
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalRoomStatsDTO getGlobalRoomStatistics(Long userId) {

        // Total Rooms
        StatDetail totalRoomsDetail = StatDetail.builder()
            .total(roomRepository.count())
            .completed(roomMembershipRepository.countCompletedRoomsForUser(userId))
            .build();

        // Easy Rooms
        StatDetail easyRoomsDetail = StatDetail.builder()
            .total(roomRepository.countByComplexity(Complexity.EASY))
            .completed(roomMembershipRepository.countCompletedRoomsByUserIdAndComplexity(userId, Complexity.EASY))  
            .build();

        // Medium Rooms
        StatDetail mediumRoomsDetail = StatDetail.builder()
            .total(roomRepository.countByComplexity(Complexity.MEDIUM))
            .completed(roomMembershipRepository.countCompletedRoomsByUserIdAndComplexity(userId, Complexity.MEDIUM))  
            .build();

        // Hard Rooms
        StatDetail hardRoomsDetail = StatDetail.builder()
            .total(roomRepository.countByComplexity(Complexity.HARD))
            .completed(roomMembershipRepository.countCompletedRoomsByUserIdAndComplexity(userId, Complexity.HARD))
            .build();

        GlobalRoomStatsDTO globalStats = GlobalRoomStatsDTO.builder()
            .totalRooms(totalRoomsDetail)
            .easyRooms(easyRoomsDetail)
            .mediumRooms(mediumRoomsDetail)
            .hardRooms(hardRoomsDetail)
            .build();

        return globalStats;
    }

}
