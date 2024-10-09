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

    private final OrchestratorFeign orchestratorFeign;
    private final CategoryRecognizer categoryRecognizer;
    private final SuggestionAlgorithm algorithmType;

    @Autowired
    public CategoryRecognizerService(OrchestratorFeign orchestratorFeign,
            LevenshteinCategoryRecognizer levenshteinRecognizer,
            ApiCategoryRecognizer apiRecognizer,
            @Value("${recognizer.algorithm.type}") String recognizerAlgorithmType) {
        this.orchestratorFeign = orchestratorFeign;

        if ("LLM".equalsIgnoreCase(recognizerAlgorithmType)) {
            this.categoryRecognizer = apiRecognizer;
            this.algorithmType = SuggestionAlgorithm.LLM;
        } else if ("LEVENSHTEIN".equalsIgnoreCase(recognizerAlgorithmType)) {
            this.categoryRecognizer = levenshteinRecognizer;
            this.algorithmType = SuggestionAlgorithm.LEVENSHTEIN;
        } else {
            throw new IllegalArgumentException("Unrecognized algorithm type: " + recognizerAlgorithmType);
        }
    }

    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        RecognizerResult recognizerResult = categoryRecognizer.recognizeCategoryAndAccuracy(message, categories);

        if (recognizerResult != null && recognizerResult.getCategory() != null) {
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accuracy(recognizerResult.getAccuracy())
                    .id(transactionId)
                    .suggestedCategoryId(recognizerResult.getCategory().getId())
                    .suggestionAlgorithm(algorithmType)
                    .build();
            orchestratorFeign.editTransaction(transactionDTO);
        } else {
            log.warn("No suggested category for message: {}", message);
        }
    }
}