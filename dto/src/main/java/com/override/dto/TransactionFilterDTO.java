package com.override.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransactionFilterDTO {

    private String category;
    private AmountRangeDTO amount;
    private String message;
    private DateRangeDTO date;
    private List<String> telegramUserNameList;
}