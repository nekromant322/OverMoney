package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CategoryRecognizerService {

  @Autowired
  private OrchestratorFeign orchestratorFeign;

  @Value("${recognizer.min-accuracy}")
  private float minAccuracy;

  protected float calculateLevenshteinDistance(String strOne, String strTwo) {
    strOne = strOne.toLowerCase();
    strTwo = strTwo.toLowerCase();
    float maxLength = Integer.max(strOne.length(), strTwo.length());
    if (maxLength > 0) {
      return (maxLength - LevenshteinDistance.getDefaultInstance().apply(strOne, strTwo))
          / maxLength;
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

  public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories,
      UUID transactionId) {
    CategoryDTO suggestedCategory = recognizeCategory(message, categories);
    Long suggestedCategoryId =
        suggestedCategory != null && calculateAccuracy(suggestedCategory, message) >= minAccuracy
            ? suggestedCategory.getId() : null;
    float accuracy =
        suggestedCategory != null ? calculateAccuracy(suggestedCategory, message) : 0.0f;
    TransactionDTO transactionDTO = TransactionDTO.builder()
        .accuracy(accuracy)
        .id(transactionId)
        .suggestedCategoryId(suggestedCategoryId)
        .build();
    orchestratorFeign.editTransaction(transactionDTO);
  }

  private float calculateAccuracy(CategoryDTO category, String message) {
    return category.getKeywords().stream()
        .map(k -> calculateLevenshteinDistance(message, k.getName()))
        .max(Float::compare)
        .orElse(0.0f);
  }
}