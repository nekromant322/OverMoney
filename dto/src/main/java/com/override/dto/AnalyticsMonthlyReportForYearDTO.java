package com.override.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsMonthlyReportForYearDTO {
    private String categoryName;
    private Map<Integer, Double> monthlyAnalytics;
}
