package com.falcon.falcon.operations.sorters;

import com.falcon.falcon.dtos.RoomDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MostUsersSorter implements SortStrategy {
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparing(RoomDTO::getTotalJoinedUsers).reversed())
                .collect(Collectors.toList());
    }
}