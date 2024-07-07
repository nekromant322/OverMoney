package com.override.orchestrator_service.filter;

import com.override.dto.AmountRangeDTO;
import com.override.dto.DateRangeDTO;
import com.override.orchestrator_service.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransactionFilter {

    private Category category;
    private AmountRangeDTO amount;
    private String message;
    private DateRangeDTO date;
    private List<Long> telegramUsersId;
}