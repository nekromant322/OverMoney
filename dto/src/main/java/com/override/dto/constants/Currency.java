package com.override.dto.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    RUB("RUB"),
    USD("USD"),
    EUR("EUR"),
    CNY("CNY"),
    KZT("KZT");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}