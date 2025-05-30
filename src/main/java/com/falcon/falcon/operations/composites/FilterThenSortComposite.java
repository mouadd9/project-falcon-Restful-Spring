package com.falcon.falcon.operations.composites;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.operations.RoomOperation;
import com.falcon.falcon.operations.filters.FilterStrategy;
import com.falcon.falcon.operations.sorters.SortStrategy;

import java.util.List;

/**
 * Main composite that applies filters first, then sorters.
 * This is the top-level operation that coordinates filtering and sorting.
 */
public class FilterThenSortComposite implements RoomOperation {
    private final FilterStrategy filterStrategy; // AndFilter (Composite)
    private final SortStrategy sortStrategy; // SorterChain (Composite)
    
    public FilterThenSortComposite(FilterStrategy filterStrategy, SortStrategy sortStrategy) {
        this.filterStrategy = filterStrategy;
        this.sortStrategy = sortStrategy;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        // First apply all filters
        List<RoomDTO> filteredRooms = filterStrategy != null ? filterStrategy.apply(rooms) : rooms;
        
        // Then apply sorting
        return sortStrategy != null ? sortStrategy.apply(filteredRooms) : filteredRooms;
    }
}