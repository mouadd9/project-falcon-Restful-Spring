package com.falcon.falcon.services;

import com.falcon.falcon.dtos.RoomDTO;
import com.falcon.falcon.dtos.RoomFilterCriteria;
import com.falcon.falcon.operations.RoomOperation;
import com.falcon.falcon.operations.composites.AndFilter;
import com.falcon.falcon.operations.composites.FilterThenSortComposite;
import com.falcon.falcon.operations.composites.SorterChain;
import com.falcon.falcon.operations.filters.FilterStrategy;
import com.falcon.falcon.operations.sorters.SortStrategy;
import com.falcon.falcon.enums.FilterType;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class RoomOperationService {
    private final Map<RoomFilterCriteria.SortBy, SortStrategy> sortingStrategies;
    private final Map<FilterType, Function<RoomFilterCriteria, FilterStrategy>> filterStrategies;

    public RoomOperationService(Map<RoomFilterCriteria.SortBy, SortStrategy> sortingStrategies, Map<FilterType, Function<RoomFilterCriteria, FilterStrategy>> filterStrategies) {
        this.sortingStrategies = sortingStrategies;
        this.filterStrategies = filterStrategies;
    }

    public List<RoomDTO> applyFiltersAndSorting(List<RoomDTO> rooms, RoomFilterCriteria criteria) {

        // Build the filter chain
        AndFilter filterChain = new AndFilter();
               
        // we iterate over the filter strategy lambda functions and pass the criteria to them to retrieve the configured FilterStrategies
        for (Function<RoomFilterCriteria, FilterStrategy> strategyFunction : filterStrategies.values()) {
            FilterStrategy filter = strategyFunction.apply(criteria);
            if (filter != null) {
                filterChain.add(filter); // we add the filter to the chain if it's not null
            }
        }

        // Build the sorter chain
        SorterChain sorterChain = new SorterChain();

        if (criteria.getSortBy() != null) {
            SortStrategy sorter = sortingStrategies.get(criteria.getSortBy());
            if (sorter != null) {
                sorterChain.add(sorter);
            }
        }

        // Create the main composite operation
        RoomOperation operation = new FilterThenSortComposite(filterChain, sorterChain);

        // Apply the operation
        return operation.apply(rooms);
    }
}