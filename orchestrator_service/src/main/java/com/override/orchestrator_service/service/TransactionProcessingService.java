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
                .build();
    }

    private String getTransactionMessage(TransactionMessageDTO transactionMessageDTO, OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) || Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage())))) {
            return transactionMessageDTO.getMessage();
        }
        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getKeyword();
    }

    private Category getTransactionCategory(TransactionMessageDTO transactionMessageDTO, OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) || Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage())))) {
            return null;
        }
        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getCategory();
    }

    private Set<String> getWords(String message) throws InstanceNotFoundException {
        String[] messageSplit = message.split(" ");
        Set<String> words = new HashSet<>();
        for (String word : messageSplit) {
            if (!word.matches(".*\\d.*")) {
                words.add(word);
            }
        }
        if (words.isEmpty()) {
            throw new InstanceNotFoundException("No keywords present in the message");
        }
        return words;
    }

    private Float getAmount(String message) throws InstanceNotFoundException {
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
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
