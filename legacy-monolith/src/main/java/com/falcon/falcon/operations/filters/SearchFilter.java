package com.falcon.falcon.operations.filters;

import com.falcon.falcon.dtos.RoomDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchFilter implements FilterStrategy {
    private String searchTerm;
    
    public SearchFilter() {
        this.searchTerm = "";
    }
    
    public SearchFilter(String searchTerm) {
        this.searchTerm = searchTerm != null ? searchTerm.toLowerCase().trim() : "";
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        if (searchTerm.isEmpty()) {
            return rooms;
        }
        
        return rooms.stream()
                .filter(room -> 
                    (room.getTitle() != null && room.getTitle().toLowerCase().contains(searchTerm)) ||
                    (room.getDescription() != null && room.getDescription().toLowerCase().contains(searchTerm))
                )
                .collect(Collectors.toList());
    }
    
    public SearchFilter withSearchTerm(String searchTerm) {
        return new SearchFilter(searchTerm);
    }
}