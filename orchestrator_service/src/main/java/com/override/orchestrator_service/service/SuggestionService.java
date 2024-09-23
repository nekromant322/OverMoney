package com.override.orchestrator_service.service;

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

    public void assessAndSaveSuggestion(UUID transactionId, Long categoryId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(transaction.getSuggestedCategoryId())
                .transaction(transaction)
                .accuracy(transaction.getAccuracy())
                .algorithm("LEVENSHTEIN")
                .isCorrect(categoryId.equals(transaction.getSuggestedCategoryId()))
                .build();
        suggestionRepository.save(suggestion);
    }
}
