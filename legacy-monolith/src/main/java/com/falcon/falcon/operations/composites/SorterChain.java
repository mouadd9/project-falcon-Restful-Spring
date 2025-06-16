package com.falcon.falcon.operations.composites;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.operations.sorters.SortStrategy;

import java.util.List;
import java.util.ArrayList;

/**
 * Composite that applies multiple sorters in sequence.
 * Each sorter is applied to the result of the previous sorter.
 */
public class SorterChain implements SortStrategy {
    private final List<SortStrategy> sorters;
    
    public SorterChain() {
        this.sorters = new ArrayList<>();
    }
    
    public SorterChain(List<SortStrategy> sorters) {
        this.sorters = new ArrayList<>(sorters);
    }
    
    public SorterChain add(SortStrategy sorter) {
        this.sorters.add(sorter);
        return this;
    }
    
    @Override
    public List<RoomDTO> apply(List<RoomDTO> rooms) {
        List<RoomDTO> result = rooms;
        for (SortStrategy sorter : sorters) {
            result = sorter.apply(result);
        }
        return result;
    }
}