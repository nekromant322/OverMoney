package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticDTO {
    private int quantitySuggestion;
    private int quantityCorrectSuggestion;
    private Map<String,Double> suggestionGroupsByAccuracy;
    private double overallPredictionAccuracy;
}
