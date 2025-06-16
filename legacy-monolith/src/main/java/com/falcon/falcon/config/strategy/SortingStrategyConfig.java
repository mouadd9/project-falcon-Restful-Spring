package com.falcon.falcon.config.strategy;

import com.falcon.falcon.dtos.RoomFilterCriteria;
import com.falcon.falcon.operations.sorters.SortStrategy;
import com.falcon.falcon.operations.sorters.MostUsersSorter;
import com.falcon.falcon.operations.sorters.NewestRoomsSorter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class SortingStrategyConfig {

    private final NewestRoomsSorter newestRoomsSorter;
    private final MostUsersSorter mostUsersSorter;

    public SortingStrategyConfig(NewestRoomsSorter newestRoomsSorter, MostUsersSorter mostUsersSorter) {
        this.newestRoomsSorter = newestRoomsSorter;
        this.mostUsersSorter = mostUsersSorter;
    }

    @Bean
    public Map<RoomFilterCriteria.SortBy, SortStrategy> sortingStrategies() {
        Map<RoomFilterCriteria.SortBy, SortStrategy> strategies = new EnumMap<>(RoomFilterCriteria.SortBy.class);
        strategies.put(RoomFilterCriteria.SortBy.NEWEST, newestRoomsSorter);
        strategies.put(RoomFilterCriteria.SortBy.MOST_USERS, mostUsersSorter);
        return strategies;
    }
}
