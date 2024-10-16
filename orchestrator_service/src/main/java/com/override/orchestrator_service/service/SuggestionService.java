package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.SuggestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SuggestionService {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SuggestionRepository suggestionRepository;

    public void createSuggestion(TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.getTransactionById(transactionDTO.getId());

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(transactionDTO.getSuggestedCategoryId())
                .transaction(transaction)
                .accuracy(transactionDTO.getAccuracy())
                .algorithm(transactionDTO.getSuggestionAlgorithm().getName())
                .isCorrect(null)
                .build();
        suggestionRepository.save(suggestion);
    }

    public void estimateSuggestionCorrectness(UUID transactionId, Long categoryId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Suggestion suggestion = transaction.getSuggestion();
        log.info("estimateSuggestionCorrectness: transaction id=" + transactionId + " suggestion= " + suggestion.toString());
        suggestion.setIsCorrect(categoryId.equals(suggestion.getSuggestedCategoryId()));
        suggestionRepository.save(suggestion);
    }
}