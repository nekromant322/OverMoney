package com.override.dto.constants;

import lombok.Getter;

@Getter
public enum Algorithm {
    LEVENSHTEIN("LEVENSHTEIN");

    private final String name;

    Algorithm(String value) {
        this.name = value;
    }
}