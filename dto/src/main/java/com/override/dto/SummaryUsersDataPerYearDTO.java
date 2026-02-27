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
public class SummaryUsersDataPerYearDTO {
    private int year;
    private List<UserIncomeExpenseCategoriesPerYearDTO> users;
}
