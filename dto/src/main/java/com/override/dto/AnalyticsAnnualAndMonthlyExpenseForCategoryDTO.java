package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsAnnualAndMonthlyExpenseForCategoryDTO {
    private Double amount;
    private String categoryName;
    private Integer month;
}
