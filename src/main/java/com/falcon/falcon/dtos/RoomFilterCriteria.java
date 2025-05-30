package com.falcon.falcon.dtos;

import com.falcon.falcon.enums.Complexity;
import com.falcon.falcon.operations.filters.CompletionFilter.CompletionStatus;
import com.falcon.falcon.operations.filters.EnrollmentFilter.EnrollmentStatus;
import lombok.Data;

@Data
public class RoomFilterCriteria {
    private Complexity complexity;
    private EnrollmentStatus enrollmentStatus;
    private CompletionStatus completionStatus;
    private String searchTerm;
    private SortBy sortBy;
    
    public enum SortBy {
        NEWEST, MOST_USERS
    }
}