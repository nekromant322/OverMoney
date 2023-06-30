package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsMonthlyIncomeForCategoryDTO {
    private Double amount;
    private String categoryName;
    private Integer month;
}
