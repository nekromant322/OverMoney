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
    private UserService userService;

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        User user = userService.getUserByUsername(transactionMessageDTO.getUsername());
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(getAmount(transactionMessageDTO.getMessage()));
        transaction.setMessage(getTransactionMessage(transactionMessageDTO, user));
        transaction.setCategory(getTransactionCategory(transactionMessageDTO, user));
        return transaction;
    }

    private String getTransactionMessage(TransactionMessageDTO transactionMessageDTO, User user) throws InstanceNotFoundException {
        if (Objects.isNull(user.getCategories()) || Objects.isNull(getMatchingKeyword(user.getCategories(), getWords(transactionMessageDTO.getMessage())))) {
            return transactionMessageDTO.getMessage();
        }
        Keyword matchingKeyword = getMatchingKeyword(user.getCategories(), getWords(transactionMessageDTO.getMessage()));
        return matchingKeyword.getKeyword();
    }

    private Category getTransactionCategory(TransactionMessageDTO transactionMessageDTO, User user) throws InstanceNotFoundException {
        if (Objects.isNull(user.getCategories()) || Objects.isNull(getMatchingKeyword(user.getCategories(), getWords(transactionMessageDTO.getMessage())))) {
            return null;
        }
        Keyword matchingKeyword = getMatchingKeyword(user.getCategories(), getWords(transactionMessageDTO.getMessage()));
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
