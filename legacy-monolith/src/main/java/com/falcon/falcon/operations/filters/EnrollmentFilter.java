package com.falcon.falcon.operations.filters;

import com.falcon.falcon.dtos.RoomDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentFilter implements FilterStrategy {
    private EnrollmentStatus status;
      
    public enum EnrollmentStatus {
        ALL, ENROLLED, NOT_ENROLLED
    }
    
    public EnrollmentFilter() {
        this.status = EnrollmentStatus.ALL;
    }
    
    public EnrollmentFilter(EnrollmentStatus status) {
        this.status = status;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        switch (status) {
            case ENROLLED:
                return rooms.stream()
                        .filter(room -> room.getIsJoined() != null && room.getIsJoined())
                        .collect(Collectors.toList());
            case NOT_ENROLLED:
                return rooms.stream()
                        .filter(room -> room.getIsJoined() == null || !room.getIsJoined())
                        .collect(Collectors.toList());
            case ALL:
            default:
                return rooms;
        }
    }
    
    public EnrollmentFilter withStatus(EnrollmentStatus status) {
        return new EnrollmentFilter(status);
    }
}