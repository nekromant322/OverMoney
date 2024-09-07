package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnalyticsDataMonthDiffDTO {
    private Integer currentMonthIncomeToPrevMonthDiff;
    private Integer currentMonthExpenseToPrevMonthDiff;
    private Integer currentMonthIncomeToPrevYearDiff;
    private Integer currentMonthExpenseToPrevYearDiff;
    private String currentMonthName;
    private String previousMonthName;
}
