package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SumTransactionPerCategoryPerPeriodDTO {

    private Long id;

    private String name;

    private Double sum;

    private String type;

    private String period;
}
