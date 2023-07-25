package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsAnnualAndMonthlyReportDTO {
    private String categoryName;
    private Map<Integer, Double> monthlyAnalytics;
    private Map<Integer, Double> shareOfMonthlyExpenses;
}
