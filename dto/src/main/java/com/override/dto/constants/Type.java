package com.override.dto.constants;

import lombok.Getter;

@Getter
public enum Type {
    INCOME("Доходы"),
    EXPENSE("Расходы");

    private final String value;

    Type(String value) {
        this.value = value;
    }

}
