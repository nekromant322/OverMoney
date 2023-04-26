package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionProcessingService {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    public Transaction processTransaction(TransactionMessageDTO transactionMessage) throws InstanceNotFoundException {
        User user = userService.getUserByUsername(transactionMessage.getUsername());
        Set<Category> categories = user.getCategories();
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(getAmount(transactionMessage.getMessage()));
        transaction.setMessage(getMessageWithoutAmount(transactionMessage.getMessage()));
        transaction.setCategory(getMatchingCategory(categories, getWords(transactionMessage.getMessage())));
        return transaction;
    }

    private Set<String> getWords(String message) throws InstanceNotFoundException {
        String[] messageSplit = message.split(" ");
        Set<String> words = new HashSet<>();
        for (String word: messageSplit) {
            if (!word.matches(".*\\d.*")) {
                words.add(word);
            }
        }
        if (words.isEmpty()) {
            throw new InstanceNotFoundException("No keywords present in the message");
        }
        return words;
    }

    private String getMessageWithoutAmount(String message) throws InstanceNotFoundException {
        String amount = getAmount(message).toString();
        return message.replaceAll(amount, "");
    }

    private Long getAmount(String message) throws InstanceNotFoundException {
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new InstanceNotFoundException("No amount stated");
    }

    private Category getMatchingCategory(Set<Category> categories, Set<String> words) {
        Category matchingCategory = categoryService.getUndefinedCategory();
        for (Category category: categories) {
            Set<Keyword> keywords = category.getKeywords();
           for (Keyword keyword: keywords) {
               for (String word: words) {
                   if (word.equals(keyword.getKeyword())) {
                       matchingCategory = keyword.getCategory();
                       break;
                   }
               }
           }
        }
        return matchingCategory;
    }
}
