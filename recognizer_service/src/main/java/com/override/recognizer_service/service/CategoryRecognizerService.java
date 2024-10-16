package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.override.dto.constants.SuggestionAlgorithm;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryRecognizerService {

    private final OrchestratorFeign orchestratorFeign;
    private final List<CategoryRecognizer> recognizers;
    private final List<SuggestionAlgorithm> algorithms;

    public CategoryRecognizerService(OrchestratorFeign orchestratorFeign,
                                     List<CategoryRecognizer> recognizers,
                                     @Value("${recognizer.algorithms}") List<String> algorithms) {
        this.orchestratorFeign = orchestratorFeign;
        this.algorithms = algorithms.stream().map(SuggestionAlgorithm::fromName).collect(Collectors.toList());
        this.recognizers = recognizers;
    }

    /**
     * Метод поиска подходящего алгоритма для подбора категории
     * Список приоритетов алгоритмов можно задать в yml в поле recognizer.algorithms
     * Если какой-то алгоритм не сможет нам подобрать категорию,
     * мы пробуем запустить подбор по следующему по приоритету алгоритму
     *
     * @param message
     * @param categories
     * @param transactionId
     */
    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        RecognizerResult recognizerResult = null;
        SuggestionAlgorithm selectedAlgorithm = null;

        for (SuggestionAlgorithm algorithm : algorithms) {
            selectedAlgorithm = algorithm;
            CategoryRecognizer recognizer = findRecognizerAlgorithm(algorithm);
            if (recognizer != null) {
                try {
                    recognizerResult = recognizer.recognizeCategoryAndAccuracy(message, categories);
                    if (recognizerResult != null && recognizerResult.getCategory() != null) {
                        break;
                    }
                } catch (Exception e) {
                    log.error("Algorithm {} failed with error: {}", algorithm, e.getMessage());
                }
            }
        }

        if (recognizerResult != null && recognizerResult.getCategory() != null) {
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accuracy(recognizerResult.getAccuracy())
                    .id(transactionId)
                    .suggestedCategoryId(recognizerResult.getCategory().getId())
                    .suggestionAlgorithm(selectedAlgorithm)
                    .build();
            orchestratorFeign.editTransaction(transactionDTO);
        } else {
            log.warn("No suggested category for message: {}", message);
        }
    }

    private CategoryRecognizer findRecognizerAlgorithm(SuggestionAlgorithm algorithm) {
        return recognizers.stream()
                .filter(recognizers -> recognizers.supportsAlgorithm(algorithm))
                .findFirst()
                .orElse(null);
    }
}