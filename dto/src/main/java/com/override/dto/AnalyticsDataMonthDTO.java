package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsDataMonthDTO {
    private String month;
    private Double totalIncome;
    private Double totalExpense;
}
