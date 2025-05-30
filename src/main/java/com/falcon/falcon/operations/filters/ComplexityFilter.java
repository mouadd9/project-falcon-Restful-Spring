package com.falcon.falcon.operations.filters;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.enums.Complexity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComplexityFilter implements FilterStrategy {
    private Complexity targetComplexity;
    
    public ComplexityFilter() {}
    
    public ComplexityFilter(Complexity complexity) {
        this.targetComplexity = complexity;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        if (targetComplexity == null) {
            return rooms; // Return all if no complexity specified
        }
        
        return rooms.stream()
                .filter(room -> room.getComplexity() == targetComplexity)
                .collect(Collectors.toList());
    }
    
    public ComplexityFilter withComplexity(Complexity complexity) {
        return new ComplexityFilter(complexity);
    }
}