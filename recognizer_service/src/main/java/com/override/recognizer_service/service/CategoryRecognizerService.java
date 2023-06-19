package com.override.recognizer_service.service;

import com.netflix.discovery.converters.Auto;
import com.override.dto.CategoryDTO;

import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transaction;
import java.util.*;

@Service
@Slf4j
public class CategoryRecognizerService {

    @Autowired
    OrchestratorFeign orchestratorFeign;


    private float calculateLevenshteinDistance(String strOne, String strTwo) {
        strOne = strOne.toLowerCase();
        strTwo = strTwo.toLowerCase();
        float maxLength = Integer.max(strOne.length(), strTwo.length());
        if (maxLength > 0) {
            return (maxLength - LevenshteinDistance.getDefaultInstance().apply(strOne, strTwo)) / maxLength;
        }
        return 0.0f;
    }

    public CategoryDTO recognizeCategory(String message, List<CategoryDTO> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        CategoryDTO[] mostSuitableCategory = {categories.get(0)};
        float[] maxLevenshteinDistance = {0};
        categories.forEach(c -> {
            c.getKeywords().add(
                    KeywordIdDTO.builder()
                    .name(c.getName())
                    .build());
        });
        categories.forEach(c -> {
            c.getKeywords().forEach(k -> {
                float currentValue = calculateLevenshteinDistance(message, k.getName());
                if (currentValue > maxLevenshteinDistance[0]) {
                    mostSuitableCategory[0] = c;
                    maxLevenshteinDistance[0] = currentValue;
                }
            });
        });
        return mostSuitableCategory[0];
    }


    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        Long suggestedCategoryId = recognizeCategory(message, categories).getId();
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSuggestedCategoryId(suggestedCategoryId);
        transactionDTO.setId(transactionId);
        orchestratorFeign.editTransaction(transactionDTO);

    }
}
