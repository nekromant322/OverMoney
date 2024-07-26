package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIncomeExpenseCategoriesPerYearDTO {
    private Long id;
    private List<SumTransactionPerYearForAccountDTO> categoryIncome;
    private List<SumTransactionPerYearForAccountDTO> categoryExpense;
}
