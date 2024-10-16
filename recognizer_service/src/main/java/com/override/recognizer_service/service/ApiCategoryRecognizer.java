package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.constants.SuggestionAlgorithm;
import com.override.recognizer_service.feign.LLMFeignClient;
import com.override.recognizer_service.llm.LLMRequestDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApiCategoryRecognizer implements CategoryRecognizer {

    private final LLMFeignClient llmFeignClient;

    @Value("${llm.model}")
    private String model;

    public ApiCategoryRecognizer(LLMFeignClient llmFeignClient) {
        this.llmFeignClient = llmFeignClient;
    }

    public LLMResponseDTO recognizeCategoryUsingAPI(String message, List<CategoryDTO> categories) {
        List<String> categoryNames = categories.stream().map(CategoryDTO::getName).collect(Collectors.toList());
        LLMRequestDTO request = new LLMRequestDTO(model, message, categoryNames);

        return llmFeignClient.recognizeCategory(request);
    }

    @Override
    public RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories) {
        LLMResponseDTO responseDTO = recognizeCategoryUsingAPI(message, categories);
        if (responseDTO != null) {
            String categoryFromAPI = responseDTO.getCategoryName();
            CategoryDTO matchedCategory = categories.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(categoryFromAPI))
                    .findFirst()
                    .orElse(null);
            float accuracy = responseDTO.getAccuracy();
            return new RecognizerResult(matchedCategory, accuracy);
        }
        return new RecognizerResult(null, 0.0f);
    }

    @Override
    public boolean supportsAlgorithm(SuggestionAlgorithm algorithm) {
        return SuggestionAlgorithm.LLM.equals(algorithm);
    }
}
