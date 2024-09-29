package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.SuggestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private SuggestionService suggestionService;

    @Test
    void assessAndEditSuggestionCategoryMatch() {
        UUID transactionId = UUID.randomUUID();
        Long categoryId = 1L;
        Long suggestedCategoryId = 1L;
        Float accuracy = 0.8f;

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .build();

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(suggestedCategoryId)
                .transaction(transaction)
                .accuracy(accuracy)
                .algorithm("LEVENSHTEIN")
                .isCorrect(null)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
        when(suggestionRepository.findSuggestionByTransaction(transaction)).thenReturn(suggestion);

        suggestionService.assessAndSaveSuggestion(transactionId, suggestedCategoryId, accuracy);
        suggestionService.editSuggestion(transactionId, categoryId);

        assert(suggestionRepository.findSuggestionByTransaction(transaction).getIsCorrect());
    }

    @Test
    void assessAndSaveSuggestionCategoryMismatch() {
        UUID transactionId = UUID.randomUUID();
        Long categoryId = 2L;
        Long suggestedCategoryId = 1L;
        Float accuracy = 0.8f;

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(suggestedCategoryId)
                .transaction(transaction)
                .accuracy(accuracy)
                .isCorrect(false)
                .algorithm("LEVENSHTEIN")
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
        when(suggestionRepository.findSuggestionByTransaction(transaction)).thenReturn(suggestion);

        suggestionService.assessAndSaveSuggestion(transactionId, suggestedCategoryId, accuracy);
        suggestionService.editSuggestion(transactionId, categoryId);

        assertFalse(suggestionRepository.findSuggestionByTransaction(transaction).getIsCorrect());
    }
}