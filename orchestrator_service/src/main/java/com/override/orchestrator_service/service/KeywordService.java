package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.KeywordId;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    public void saveKeyword(Keyword keyword) {
        keywordRepository.save(keyword);
    }

    public void setKeywordCategory(UUID transactionId, Long categoryId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Category category = categoryService.getCategoryById(categoryId);
        Keyword keyword = new Keyword();
        keyword.setKeywordId(new KeywordId(transaction.getMessage(), category.getAccount().getId()));
        keyword.setCategory(category);
        saveKeyword(keyword);
    }
}