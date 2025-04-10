package com.override.recognizer_service.service.category;

import com.override.dto.CategoryDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractApiCategoryRecognizer implements CategoryRecognizer {

    @Override
    public RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories) {
        log.info("на распознание получено сообщени: {}", message);
        LLMResponseDTO responseDTO = recognizeCategoryUsingAPI(message, categories);
        if (responseDTO != null) {
            String categoryFromAPI = responseDTO.getCategoryName();
            CategoryDTO matchedCategory = categories.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(categoryFromAPI))
                    .findFirst()
                    .orElse(null);
            float accuracy = responseDTO.getAccuracy();
            log.info("Предполагаемая категория: {}, {}", categoryFromAPI, accuracy);
            return new RecognizerResult(matchedCategory, accuracy);
        }
        return new RecognizerResult(null, 0.0f);
    }

    abstract public LLMResponseDTO recognizeCategoryUsingAPI(String message, List<CategoryDTO> categories);
}
