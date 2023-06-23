package com.override.orchestrator_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DefineServiceTest {

    @InjectMocks
    private DefineService defineService;

    @Mock
    private KeywordService keywordService;

    @Mock
    private TransactionService transactionService;

    @Test
    public void defineTransactionByTransactionIdAndCategoryIdSetAndAssociate() {
        UUID transactionId = UUID.randomUUID();
        Long categoryId = 123L;

        defineService.defineTransactionCategoryByTransactionIdAndCategoryId(transactionId, categoryId);

        verify(transactionService, times(1))
                .setCategoryForAllUndefinedTransactionsWithSameKeywords(transactionId, categoryId);
        verify(keywordService, times(1))
                .associateTransactionsKeywordWithCategory(transactionId, categoryId);
    }

    @Test
    public void undefineTransactionByTransactionIdRemovesTransactionAndKeyword() {
        UUID transactionId = UUID.randomUUID();

        defineService.undefineTransactionCategoryAndKeywordCategory(transactionId);

        verify(transactionService, times(1))
                .removeCategoryFromTransactionsWithSameMessage(transactionId);
        verify(keywordService, times(1))
                .removeCategoryFromKeywordByTransactionId(transactionId);
    }
}
