package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.override.dto.constants.SuggestionAlgorithm;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CategoryRecognizerService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @Autowired(required = false)
    public LevenshteinCategoryRecognizer levenshteinRecognizer;

    @Autowired
    public ApiCategoryRecognizer apiRecognizer;

    @Value("${recognizer.algorithm.type}")
    private String recognizerAlgorithmType;

    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        CategoryDTO suggestedCategory = null;
        float accuracy = 0.0f;
        SuggestionAlgorithm algorithmType = null;

        if ("LLM".equalsIgnoreCase(recognizerAlgorithmType)) {
            algorithmType = SuggestionAlgorithm.LLM;
            suggestedCategory = apiRecognizer.getSuggestedCategory(message, categories);
            accuracy = apiRecognizer.getAccuracy(message, categories);
        } else if ("LEVENSHTEIN".equalsIgnoreCase(recognizerAlgorithmType)) {
            algorithmType = SuggestionAlgorithm.LEVENSHTEIN;
            suggestedCategory = levenshteinRecognizer.getSuggestedCategory(message, categories);
            accuracy = levenshteinRecognizer.getAccuracy(message, categories);
        }

        if (suggestedCategory != null) {
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accuracy(accuracy)
                    .id(transactionId)
                    .suggestedCategoryId(suggestedCategory.getId())
                    .suggestionAlgorithm(algorithmType)
                    .build();
            orchestratorFeign.editTransaction(transactionDTO);
        } else {
            log.warn("No suggested category for message: {}", message);
        }
    }
}

