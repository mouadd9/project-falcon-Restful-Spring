package com.falcon.falcon.operations.filters;

import com.falcon.falcon.dtos.RoomDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompletionFilter implements FilterStrategy {
    private CompletionStatus status;
    
    public enum CompletionStatus {
        ALL, COMPLETED, IN_PROGRESS, NOT_STARTED
    }
    
    public CompletionFilter() {
        this.status = CompletionStatus.ALL;
    }
    
    public CompletionFilter(CompletionStatus status) {
        this.status = status;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        switch (status) {
            case COMPLETED:
                return rooms.stream()
                        .filter(room -> room.getPercentageCompleted() == 100)
                        .collect(Collectors.toList());
            case IN_PROGRESS:
                return rooms.stream()
                        .filter(room -> room.getPercentageCompleted() > 0 && room.getPercentageCompleted() < 100)
                        .collect(Collectors.toList());
            case NOT_STARTED:
                return rooms.stream()
                        .filter(room -> room.getPercentageCompleted() == 0)
                        .collect(Collectors.toList());
            case ALL:
            default:
                return rooms;
        }
    }
    
    public CompletionFilter withStatus(CompletionStatus status) {
        return new CompletionFilter(status);
    }
}