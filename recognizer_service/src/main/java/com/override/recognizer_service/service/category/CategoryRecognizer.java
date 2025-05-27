package com.override.recognizer_service.service.category;

import com.override.dto.CategoryDTO;
import com.override.dto.constants.SuggestionAlgorithm;

import java.util.List;

public interface CategoryRecognizer {
    RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories);

    SuggestionAlgorithm getAlgorithm();
}
