package com.override.dto.constants;

import lombok.Getter;

@Getter
public enum SuggestionAlgorithm {
    LEVENSHTEIN("LEVENSHTEIN"),
    LLM("LLM");

    private final String name;

    SuggestionAlgorithm(String value) {
        this.name = value;
    }

    /**
     * Метод преобразования строки в объект Enum
     * Необходим для определения доступности алгоритма в системе
     */
    public static SuggestionAlgorithm fromName(String name) {
        switch (name.toUpperCase()) {
            case "LLM":
                return LLM;
            case "LEVENSHTEIN":
                return LEVENSHTEIN;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + name);
        }
    }
}