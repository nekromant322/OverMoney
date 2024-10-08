package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LevenshteinCategoryRecognizer implements CategoryRecognizer {

    protected float calculateLevenshteinDistance(String strOne, String strTwo) {
        strOne = strOne.toLowerCase();
        strTwo = strTwo.toLowerCase();
        float maxLength = Integer.max(strOne.length(), strTwo.length());
        if (maxLength > 0) {
            return (maxLength - LevenshteinDistance.getDefaultInstance().apply(strOne, strTwo)) / maxLength;
        }
        return 0.0f;
    }

    @Override
    public CategoryDTO getSuggestedCategory(String message, List<CategoryDTO> categories) {
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

    @Override
    public float getAccuracy(String message, List<CategoryDTO> categories) {
        return categories.stream()
                            .flatMap(category -> category.getKeywords().stream())
                            .map(k -> calculateLevenshteinDistance(message, k.getName()))
                            .max(Float::compare)
                            .orElse(0.0f);
    }
}