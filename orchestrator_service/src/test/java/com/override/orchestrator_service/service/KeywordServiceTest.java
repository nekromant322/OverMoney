package com.override.orchestrator_service.service;

import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.KeywordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeywordServiceTest {

    @InjectMocks
    private KeywordService keywordService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private KeywordRepository keywordRepository;

    @Test
    public void setKeywordCategoryThrowExceptionWhenCategoryNotFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(categoryService.getCategoryById(category.getId())).thenThrow(CategoryNotFoundException.class);

        assertThrows(CategoryNotFoundException.class, () ->
                keywordService.setKeywordCategory(transaction.getId(), category.getId()));
    }

    @Test
    public void setKeywordCategoryThrowExceptionWhenTransactionNotFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(transactionService.getTransactionById(transaction.getId())).thenThrow(TransactionNotFoundException.class);

        assertThrows(TransactionNotFoundException.class, () ->
                keywordService.setKeywordCategory(transaction.getId(), category.getId()));
    }

    @Test
    public void keywordRepositorySaveKeywordWhenCategoryAndTransactionFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setMessage("message");
        final Keyword keyword = new Keyword(transaction.getMessage(), category);

        keywordService.saveKeyword(keyword);

        verify(keywordRepository, times(1)).save(any(Keyword.class));
    }

    @Test
    public void setKeywordCategorySaveKeywordWhenCategoryAndTransactionFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(categoryService.getCategoryById(category.getId())).thenReturn(category);
        when(transactionService.getTransactionById(transaction.getId())).thenReturn(transaction);

        keywordService.setKeywordCategory(transaction.getId(), category.getId());

        verify(keywordRepository, times(1)).save(any(Keyword.class));
    }
}