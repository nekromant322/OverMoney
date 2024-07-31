package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.KeywordRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    @Lazy
    private TransactionService transactionService;
    @Autowired
    private CategoryService categoryService;

    @Value("${clean-deprecated-keywords.max-days}")
    private int maxDays;

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
        Long accountId = transaction.getTelegramUserId();

        KeywordId keywordId = new KeywordId(transaction.getMessage(), accountId);
        Optional<Keyword> optionalKeyword = keywordRepository.findByKeywordId(keywordId);

        Keyword keyword;
        if (optionalKeyword.isPresent()) {
            keyword = optionalKeyword.get();
        } else {
            keyword = new Keyword();
            keyword.setKeywordId(keywordId);
            keyword.setLastUsed(LocalDateTime.now());
        }

        keyword.setCategory(category);
        keywordRepository.save(keyword);
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

    @Scheduled(fixedRateString = "#{${clean-deprecated-keywords.interval} * 24 * 60 * 60 * 1000}")
    @SchedulerLock(name = "cleanKeyword", lockAtLeastFor = "10m", lockAtMostFor = "15m")
    @Transactional
    public void cleanDepricatedKeywords() {
        LocalDateTime maxDate = LocalDateTime.now().minusDays(maxDays);
        keywordRepository.deleteDepricatedKeywords(maxDate);
    }

    public void updateLastUsed(String keywordText, Long accountId) {
        KeywordId keywordId = new KeywordId(keywordText, accountId);
        Optional<Keyword> optionalKeyword = keywordRepository.findByKeywordId(keywordId);
        Keyword keyword;
        if (optionalKeyword.isPresent()) {
            keyword = optionalKeyword.get();
            keyword.setLastUsed(LocalDateTime.now());
        } else {
            keyword = new Keyword();
            keyword.setKeywordId(keywordId);
            keyword.setLastUsed(LocalDateTime.now());
        }
        keywordRepository.save(keyword);
    }
}
