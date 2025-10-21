package com.override.orchestrator_service.service;

import com.override.dto.StatisticDTO;
import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.repository.SuggestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {
    @InjectMocks
    private StatisticService statisticService;
    @Mock
    private SuggestionRepository suggestionRepository;


    @Test
    void calculateSuggestionsStatistic_WithMixedSuggestions_ReturnsCorrectStatistics() {
        List<Suggestion> suggestions = Arrays.asList(
            createSuggestion(0.8f, true),
            createSuggestion(0.7f, true),
            createSuggestion(0.3f, false),
            createSuggestion(0.2f, null)
        );
        when(suggestionRepository.findAllByAlgorithm("DEEPSEEK")).thenReturn(suggestions);


        StatisticDTO result = statisticService.calculateSuggestionsStatistic();

        assertNotNull(result);
        assertEquals(2, result.getQuantityCorrectSuggestion());
        assertEquals(4, result.getQuantitySuggestion());
        assertEquals(66.6, result.getOverallPredictionAccuracy(), 0.1);

    }

    @Test
    void calculateSuggestionsStatistic_WithEmptyList_ReturnsZeroStatistics() {
        when(suggestionRepository.findAllByAlgorithm("DEEPSEEK")).thenReturn(List.of());

        StatisticDTO result = statisticService.calculateSuggestionsStatistic();

        assertNotNull(result);
        assertEquals(0, result.getQuantitySuggestion());
        assertEquals(0, result.getQuantityCorrectSuggestion());
        assertEquals(0.0, result.getOverallPredictionAccuracy(), 0.01);

        Map<String, Double> accuracyGroups = result.getSuggestionGroupsByAccuracy();
        assertEquals(4, accuracyGroups.size());
        assertEquals(0.0, accuracyGroups.get("0-0.5"), 0.01);
        assertEquals(0.0, accuracyGroups.get("0.5-0.7"), 0.01);
        assertEquals(0.0, accuracyGroups.get("0.7-0.9"), 0.01);
        assertEquals(0.0, accuracyGroups.get("0.9-1.0"), 0.01);
    }


    private Suggestion createSuggestion (Float accuracy, Boolean isCorrect){
        return Suggestion.builder()
                .accuracy(accuracy)
                .isCorrect(isCorrect)
                .build();
    }
}
