package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnalyticsDataPerMonthDTO {
    private double[] monthsIncome;
    private double[] monthsExpense;
}
