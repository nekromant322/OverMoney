package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import lombok.Getter;

@Getter
public class RecognizerResult {

    private final CategoryDTO category;
    private final float accuracy;

    public RecognizerResult(CategoryDTO category, float accuracy) {
        this.category = category;
        this.accuracy = accuracy;
    }
}

