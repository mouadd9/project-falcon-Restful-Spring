package com.falcon.falcon.dtos.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatDetail {
    private long total;     // Total available
    private long completed;  // User's completed
}
