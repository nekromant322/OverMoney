package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.override.dto.TransactionMessageDTO;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionProcessingService {

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecognizerFeign recognizerFeign;

    private static final String SPACE = " ";

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(transactionMessageDTO.getChatId());
        List<CategoryDTO> categories = categoryService.findCategoriesListByUserId(transactionMessageDTO.getChatId());

        String transactionMessage = getTransactionMessage(transactionMessageDTO, overMoneyAccount);
        Long suggestedCategoryId = recognizerFeign.recognizeCategory(transactionMessage, categories).getId();
        return Transaction.builder()
                .account(overMoneyAccount)
                .amount(getAmount(transactionMessageDTO.getMessage()))
                .message(transactionMessage)
                .category(getTransactionCategory(transactionMessageDTO, overMoneyAccount))
                .date(transactionMessageDTO.getDate())
                .suggestedCategoryId(suggestedCategoryId)
                .build();
    }

    private String getTransactionMessage(TransactionMessageDTO transactionMessageDTO, OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) ||
                Objects.isNull(getMatchingCategory(overMoneyAccount.getCategories(),
                        getWords(transactionMessageDTO.getMessage()))) &&
                        Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(),
                                getWords(transactionMessageDTO.getMessage())))) {
            return getWords(transactionMessageDTO.getMessage());
        }

        Category matchingCategory = getMatchingCategory(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        if (matchingCategory != null) {
            return matchingCategory.getName();
        }

        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getKeywordId().getName();
    }

    private Category getTransactionCategory(TransactionMessageDTO transactionMessageDTO, OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) ||
                Objects.isNull(getMatchingCategory(overMoneyAccount.getCategories(),
                        getWords(transactionMessageDTO.getMessage()))) &&
                        Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(),
                                getWords(transactionMessageDTO.getMessage())))) {
            return null;
        }

        Category matchingCategory = getMatchingCategory(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        if (matchingCategory != null) {
            return matchingCategory;
        }

        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getCategory();
    }

    private Category getMatchingCategory(Set<Category> categories, String words) {
        Category matchingCategory = null;
        for (Category category : categories) {
            if (words.equalsIgnoreCase(category.getName())) {
                matchingCategory = category;
                break;
            }
        }
        return matchingCategory;
    }

    private String getWords(String message) throws InstanceNotFoundException {
        int lastSpaceIndex = message.lastIndexOf(SPACE);
        String words = message.substring(0, lastSpaceIndex).trim();
        if (words.isEmpty()) {
            throw new InstanceNotFoundException("No keywords present in the message");
        }
        return words;
    }

    private Float getAmount(String message) throws InstanceNotFoundException {
        Pattern pattern = Pattern.compile("[0-9]*[.|,]{0,1}[0-9]+$");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Float.parseFloat(matcher.group());
        }
        throw new InstanceNotFoundException("No amount stated");
    }

    private Keyword getMatchingKeyword(Set<Category> categories, String words) {
        Keyword matchingKeyword = null;
        for (Category category : categories) {
            Set<Keyword> keywordsSet = category.getKeywords();
            for (Keyword keyword : keywordsSet) {
                if (words.equalsIgnoreCase(keyword.getKeywordId().getName())) {
                    matchingKeyword = keyword;
                    break;
                }
            }
        }
        return matchingKeyword;
    }
}
