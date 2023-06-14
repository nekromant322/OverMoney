package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CategoryRecognizerService {

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
        CategoryDTO[] mostSuitableCategory = {categories.get(0)};
        float[] maxLevenshteinDistance = {0};
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
}
