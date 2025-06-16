package com.falcon.falcon.config.strategy;

import com.falcon.falcon.dtos.RoomFilterCriteria;
import com.falcon.falcon.enums.FilterType;
import com.falcon.falcon.operations.filters.CompletionFilter;
import com.falcon.falcon.operations.filters.ComplexityFilter;
import com.falcon.falcon.operations.filters.EnrollmentFilter;
import com.falcon.falcon.operations.filters.FilterStrategy;
import com.falcon.falcon.operations.filters.SearchFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class FilteringStrategyConfig {

    private final ComplexityFilter complexityFilter;
    private final EnrollmentFilter enrollmentFilter;
    private final CompletionFilter completionFilter;
    private final SearchFilter searchFilter;

    public FilteringStrategyConfig(ComplexityFilter complexityFilter, EnrollmentFilter enrollmentFilter, CompletionFilter completionFilter, SearchFilter searchFilter) {
        this.complexityFilter = complexityFilter;
        this.enrollmentFilter = enrollmentFilter;
        this.completionFilter = completionFilter;
        this.searchFilter = searchFilter;
    }

    // in this map we will map FilterType to a Lambda Function that takes RoomFilterCriteria and returns a configured FilterStrategy
    // this allows us to dynamically create filter strategies based on the criteria provided 
    @Bean
    public Map<FilterType, Function<RoomFilterCriteria, FilterStrategy>> filterStrategies() {
        Map<FilterType, Function<RoomFilterCriteria, FilterStrategy>> strategies = new EnumMap<>(FilterType.class);
        strategies.put(FilterType.COMPLEXITY, criteria -> criteria.getComplexity() != null ? complexityFilter.withComplexity(criteria.getComplexity()) : null);
        strategies.put(FilterType.ENROLLMENT_STATUS, criteria -> criteria.getEnrollmentStatus() != null ? enrollmentFilter.withStatus(criteria.getEnrollmentStatus()) : null);
        strategies.put(FilterType.COMPLETION_STATUS, criteria -> criteria.getCompletionStatus() != null ? completionFilter.withStatus(criteria.getCompletionStatus()) : null);
        strategies.put(FilterType.SEARCH_TERM, criteria -> (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) ? searchFilter.withSearchTerm(criteria.getSearchTerm()) : null);
        return strategies;
    }
}
