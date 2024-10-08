package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import java.util.List;

public interface CategoryRecognizer {
    CategoryDTO getSuggestedCategory(String message, List<CategoryDTO> categories);

    float getAccuracy(String message, List<CategoryDTO> categories);
}
