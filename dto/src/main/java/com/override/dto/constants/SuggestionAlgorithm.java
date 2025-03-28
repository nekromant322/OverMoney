package com.override.dto.constants;

import lombok.Getter;

@Getter
public enum SuggestionAlgorithm {
    LEVENSHTEIN("LEVENSHTEIN"),
    LLM("LLM"),
    DEEPSEEK("DEEPSEEK");

    private final String name;

    SuggestionAlgorithm(String value) {
        this.name = value;
    }
}