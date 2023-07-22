package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public void saveKeywordsList(List<Keyword> keywordList) {
        keywordRepository.saveAll(keywordList);
    }

    public void updateCategory(Long categoryToMergeId, Long categoryToChangeId) {
        keywordRepository.updateCategoryId(categoryToMergeId, categoryToChangeId);
    }

    public void associateTransactionsKeywordWithCategory(UUID transactionId, Long categoryId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Category category = categoryService.getCategoryById(categoryId);
        Keyword keyword = new Keyword();
        keyword.setKeywordId(new KeywordId(transaction.getMessage(), category.getAccount().getId()));
        keyword.setCategory(category);
        saveKeyword(keyword);
    }

    public void removeCategoryFromKeywordByTransactionId(UUID transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        KeywordId keywordId = new KeywordId(transaction.getMessage(),
                transaction.getAccount().getId());
        keywordRepository.removeCategoryId(keywordId);
    }

    public List<Keyword> findAllByOverMoneyAccount(OverMoneyAccount overMoneyAccount) {
        return keywordRepository.findAllByOverMoneyAccount(overMoneyAccount.getId());
    }

    public void setKeywordsFromCategoryDTO(CategoryDTO categoryDTO, Category category, Long accountId) {
        List<KeywordIdDTO> keywordIdDTOList = categoryDTO.getKeywords();
        List<Keyword> keywordList = new ArrayList<>();

        for (KeywordIdDTO keywordIdDTO : keywordIdDTOList) {
            KeywordId keywordId = new KeywordId();
            keywordId.setAccountId(accountId);
            keywordId.setName(keywordIdDTO.getName());
            Keyword keyword = new Keyword();
            keyword.setKeywordId(keywordId);
            keyword.setCategory(category);
            keywordList.add(keyword);
        }
        keywordRepository.saveAll(keywordList);
    }
}
