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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private SuggestionService suggestionService;

    @Test
    void assessAndSaveSuggestionCategoryMatch() {
        UUID transactionId = UUID.randomUUID();
        Long userSelectedCategoryId = 1L;
        Long suggestedCategoryId = 1L;
        Float accuracy = 0.8f;

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .suggestedCategoryId(suggestedCategoryId)
                .accuracy(accuracy)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(transaction.getSuggestedCategoryId())
                .transaction(transaction)
                .accuracy(transaction.getAccuracy())
                .isCorrect(true)
                .algorithm("LEVENSHTEIN")
                .build();

        suggestionService.assessAndSaveSuggestion(transactionId, userSelectedCategoryId);

        assertTrue(suggestion.isCorrect());
        assertEquals(suggestion.getAccuracy(), transaction.getAccuracy());
        verify(suggestionRepository).save(any(Suggestion.class));
        verify(suggestionRepository).save(argThat(s -> s.isCorrect()));
    }

    @Test
    void assessAndSaveSuggestionCategoryMismatch() {
        UUID transactionId = UUID.randomUUID();
        Long userSelectedCategoryId = 2L;
        Long suggestedCategoryId = 1L;
        Float accuracy = 0.8f;

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .suggestedCategoryId(suggestedCategoryId)
                .accuracy(accuracy)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(transaction.getSuggestedCategoryId())
                .transaction(transaction)
                .accuracy(transaction.getAccuracy())
                .isCorrect(false)
                .algorithm("LEVENSHTEIN")
                .build();

        suggestionService.assessAndSaveSuggestion(transactionId, userSelectedCategoryId);

        assertFalse(suggestion.isCorrect());
        assertEquals(suggestion.getAccuracy(), transaction.getAccuracy());
        verify(suggestionRepository).save(any(Suggestion.class));
        verify(suggestionRepository).save(argThat(s -> !s.isCorrect()));
    }
}