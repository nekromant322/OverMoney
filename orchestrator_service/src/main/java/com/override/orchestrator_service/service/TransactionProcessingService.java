package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.override.dto.TransactionMessageDTO;
import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionProcessingService {

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(transactionMessageDTO.getChatId());

        return Transaction.builder()
                .account(overMoneyAccount)
                .amount(getAmount(transactionMessageDTO.getMessage()))
                .message(getTransactionMessage(transactionMessageDTO, overMoneyAccount))
                .category(getTransactionCategory(transactionMessageDTO, overMoneyAccount))
                .date(transactionMessageDTO.getDate())
                .build();
    }

    private String getTransactionMessage(TransactionMessageDTO transactionMessageDTO, OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) ||
                Objects.isNull(getMatchingCategory(overMoneyAccount.getCategories(),
                        getWords(transactionMessageDTO.getMessage()))) &&
                Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(),
                        getWords(transactionMessageDTO.getMessage())))) {
            StringBuilder message = new StringBuilder();
            getWords(transactionMessageDTO.getMessage()).forEach(word -> message.append(word).append(" "));
            return message.toString().trim();
        }

        Category matchingCategory = getMatchingCategory(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        if (matchingCategory != null) {
            return matchingCategory.getName();
        }

        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getKeyword();
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

    private Category getMatchingCategory(Set<Category> categories, Set<String> words) {
        Category matchingCategory = null;
        for (Category category : categories) {
            for (String word : words) {
                if (word.equalsIgnoreCase(category.getName())) {
                    matchingCategory = category;
                    break;
                }
            }
        }
        return matchingCategory;
    }

    private Set<String> getWords(String message) throws InstanceNotFoundException {
        String[] messageSplit = message.split(" ");
        Set<String> words = new HashSet<>();
        for (String word : messageSplit) {
            if (!word.matches("^[0-9]*[.|,]{0,1}[0-9]+$")) {
                words.add(word);
            }
        }
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

    private Keyword getMatchingKeyword(Set<Category> categories, Set<String> words) {
        Keyword matchingKeyword = null;
        for (Category category : categories) {
            Set<Keyword> keywords = category.getKeywords();
            for (Keyword keyword : keywords) {
                for (String word : words) {
                    if (word.equalsIgnoreCase(keyword.getKeyword())) {
                        matchingKeyword = keyword;
                        break;
                    }
                }
            }
        }
        return matchingKeyword;
    }
}
