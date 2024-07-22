package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TransactionSummaryDTO {
    private List<MonthSumTransactionByTypeCategoryDTO> sumIncome;
    private List<MonthSumTransactionByTypeCategoryDTO> sumExpense;
}
