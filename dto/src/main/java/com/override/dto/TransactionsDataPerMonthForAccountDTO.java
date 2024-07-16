package com.override.dto;

import com.override.dto.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionsDataPerMonthForAccountDTO {
    private Type type;
    private int month;
    private Double sum;
}
