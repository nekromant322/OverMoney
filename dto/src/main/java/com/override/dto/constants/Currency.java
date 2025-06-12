package com.override.dto.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    RUB("RUB"),
    USD("USD"),
    EUR("EUR"),
    CNY("CNY"),
    KZT("KZT");

    @JsonValue
    private final String code;
}