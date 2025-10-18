package com.override.orchestrator_service.service;

import com.override.dto.StatisticDTO;
import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticService {
    @Autowired
    private SuggestionRepository suggestionRepository;

    public StatisticDTO calculateStatistic() {
        List<Suggestion> suggestionList = suggestionRepository.findAllByAlgorithm("DEEPSEEK");
        return StatisticDTO.builder()
                .quantitySuggestion(suggestionList.size())
                .quantityCorrectSuggestion((int) suggestionList.stream()
                        .filter(s -> Boolean.TRUE.equals(s.getIsCorrect()))
                        .count())
                .overallPredictionAccuracy(getOverallPredictionAccuracy(suggestionList))
                .suggestionGroupsByAccuracy(getSuggestionGroupsByAccuracy(suggestionList))
                .build();
    }

    private double getOverallPredictionAccuracy(List<Suggestion> suggestionList) {
        long countCorrectSuggestion = suggestionList.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsCorrect()))
                .count();
        long countAllSuggestion = suggestionList.stream()
                .filter(suggestion -> suggestion.getIsCorrect() != null)
                .count();
        return countAllSuggestion == 0.0 ? 0.0 : (double) countCorrectSuggestion / countAllSuggestion * 100;
    }

    private Map<String, Double> getSuggestionGroupsByAccuracy(List<Suggestion> suggestionList) {
        Map<String, Integer> groups = new HashMap<>(Map.of(
                "0-0.5", 0,
                "0.5-0.7", 0,
                "0.7-0.9", 0,
                "0.9-1.0", 0
        ));
        suggestionList.forEach(suggestion -> {
            Float accuracy = suggestion.getAccuracy();
            if (accuracy < 0.5) {
                groups.put("0-0.5", groups.get("0-0.5") + 1);
            } else if (accuracy < 0.7) {
                groups.put("0.5-0.7", groups.get("0.5-0.7") + 1);
            } else if (accuracy < 0.9) {
                groups.put("0.7-0.9", groups.get("0.7-0.9") + 1);
            } else if (accuracy <= 1.0) {
                groups.put("0.9-1.0", groups.get("0.9-1.0") + 1);
            }
        });
        Map<String, Double> groupsByPercent = new HashMap<>();
        groups.forEach((range, quantity) -> {
            if (suggestionList.isEmpty()) {
                groupsByPercent.put(range, 0.0);
            } else {
                groupsByPercent.put(range, (double) quantity / suggestionList.size() * 100);
            }
        });
        return groupsByPercent;
    }
}
