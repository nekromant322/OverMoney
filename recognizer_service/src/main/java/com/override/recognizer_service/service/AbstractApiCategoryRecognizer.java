package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import com.override.recognizer_service.service.category.CategoryRecognizer;
import com.override.recognizer_service.service.category.RecognizerResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractApiCategoryRecognizer implements CategoryRecognizer {

    @Override
    public RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories) {
        log.debug("на распознание получено сообщени: {}", message);
        LLMResponseDTO responseDTO = recognizeCategoryUsingAPI(message, categories);
        if (responseDTO != null) {
            String categoryFromAPI = responseDTO.getCategoryName();
            CategoryDTO matchedCategory = categories.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(categoryFromAPI))
                    .findFirst()
                    .orElse(null);
            float accuracy = responseDTO.getAccuracy();
            log.debug("Предполагаемая категория: {}, {}", categoryFromAPI, accuracy);
            return new RecognizerResult(matchedCategory, accuracy);
        }
        return new RecognizerResult(null, 0.0f);
    }

    abstract public LLMResponseDTO recognizeCategoryUsingAPI(String message, List<CategoryDTO> categories);
}
