package com.override.orchestrator_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DefineService {
    @Autowired
    private KeywordService keywordService;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    public void defineTransactionCategoryByTransactionIdAndCategoryId(UUID transactionId, Long categoryId) {
        transactionService.setCategoryForAllUndefinedTransactionsWithSameKeywords(transactionId, categoryId);
        keywordService.associateTransactionsKeywordWithCategory(transactionId, categoryId);
    }

    @Transactional
    public void undefineTransactionCategoryAndKeywordCategory(UUID transactionId) {
        transactionService.removeCategoryFromTransactionsWithSameMessage(transactionId);
        keywordService.removeCategoryFromKeywordByTransactionId(transactionId);
    }
}
