package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import java.util.List;

public interface CategoryRecognizer {
    RecognizerResult recognizeCategoryAndAccuracy(String message, List<CategoryDTO> categories);
}
