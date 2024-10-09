package com.override.orchestrator_service.service;

import com.override.dto.constants.SuggestionAlgorithm;
import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SuggestionService {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SuggestionRepository suggestionRepository;

    public void createSuggestion(UUID transactionId, Long suggestedCategoryId,
            Float accuracy, SuggestionAlgorithm algorithm) {
        Transaction transaction = transactionService.getTransactionById(transactionId);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(suggestedCategoryId)
                .transaction(transaction)
                .accuracy(accuracy)
                .algorithm(algorithm.getName())
                .isCorrect(null)
                .build();
        suggestionRepository.save(suggestion);
    }

    public void estimateSuggestionCorrectness(UUID transactionId, Long categoryId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Suggestion suggestion = transaction.getSuggestion();

        suggestion.setIsCorrect(suggestion.getSuggestedCategoryId().equals(categoryId));
        suggestionRepository.save(suggestion);
    }
}