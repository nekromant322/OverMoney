package com.override.orchestrator_service.mapper;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.CategoryDTO;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

    public AnalyticsDataDTO mapAnalyticsDataToJsonResponse(CategoryDTO categoryDTO, Long sumOfAmount) {
        return AnalyticsDataDTO.builder()
                .category(categoryDTO)
                .sumOfTransactions(sumOfAmount)
                .build();
    }
}
