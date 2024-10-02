package com.override.orchestrator_service.service;

import com.override.dto.constants.SuggestionAlgorithm;
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
    void createSuggestionTest() {
        UUID transactionId = UUID.randomUUID();
        Long suggestedCategoryId = 1L;
        Float accuracy = 0.8f;

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .build();

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(suggestedCategoryId)
                .transaction(transaction)
                .accuracy(accuracy)
                .algorithm(SuggestionAlgorithm.LEVENSHTEIN.getName())
                .isCorrect(null)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
        when(suggestionRepository.findSuggestionByTransaction(transaction)).thenReturn(suggestion);

        suggestionService.createSuggestion(transactionId, suggestedCategoryId, accuracy);

        assertEquals(suggestionRepository.findSuggestionByTransaction(transaction), suggestion);
    }

    @Test
    void estimateSuggestionCorrectnessTest() {
        UUID transactionId = UUID.randomUUID();
        Long suggestedCategoryId = 1L;
        Long categoryId = 2L;
        Float accuracy = 0.8f;

        Transaction transaction = mock(Transaction.class);

        Suggestion suggestion = Suggestion.builder()
                .suggestedCategoryId(suggestedCategoryId)
                .transaction(transaction)
                .accuracy(accuracy)
                .algorithm(SuggestionAlgorithm.LEVENSHTEIN.getName())
                .isCorrect(null)
                .build();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
        when(transaction.getSuggestion()).thenReturn(suggestion);

        suggestionService.estimateSuggestionCorrectness(transactionId, categoryId);

        assertFalse(suggestion.getIsCorrect());
    }
}