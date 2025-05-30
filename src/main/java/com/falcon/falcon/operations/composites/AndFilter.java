package com.falcon.falcon.operations.composites;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.operations.filters.FilterStrategy;

import java.util.List;
import java.util.ArrayList;

/**
 * Composite that applies multiple filters sequentially (AND logic).
 * Each filter is applied to the result of the previous filter.
 */
public class AndFilter implements FilterStrategy {
    private final List<FilterStrategy> filters;
    
    public AndFilter() {
        this.filters = new ArrayList<>(); 
    }
    
    public AndFilter(List<FilterStrategy> filters) {
        this.filters = new ArrayList<>(filters);
    }
    
    public AndFilter add(FilterStrategy filter) {
        this.filters.add(filter);
        return this;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        List<RoomDTO> result = rooms;
        for (FilterStrategy filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }
}