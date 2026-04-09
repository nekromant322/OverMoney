package com.override.recognizer_service.service.category;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.constants.SuggestionAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Order(3)
@ConditionalOnProperty(name = "recognizer.levenshtein-algo", havingValue = "ACTIVE")
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
    public RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories) {
        if (categories.isEmpty()) {
            return null;
        }
        CategoryDTO mostSuitableCategory = null;
        float maxLevenshteinDistance = 0.0f;

        for (CategoryDTO category : categories) {
            category.getKeywords().add(
                     KeywordIdDTO.builder()
                    .name(category.getName())
                    .build());

            for (KeywordIdDTO keywordId : category.getKeywords()) {
                float currentValue = calculateLevenshteinDistance(message, keywordId.getName());
                if (currentValue > maxLevenshteinDistance) {
                    mostSuitableCategory = category;
                    maxLevenshteinDistance = currentValue;
                }
            }
        }

        return new RecognizerResult(mostSuitableCategory, maxLevenshteinDistance);
    }

    @Override
    public SuggestionAlgorithm getAlgorithm() {
        return SuggestionAlgorithm.LEVENSHTEIN;
    }
}