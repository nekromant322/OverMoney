package com.override.orchestrator_service.constants;

import lombok.Getter;

@Getter
public enum Type {
    INCOME("Доходы"),
    EXPENSE("Расходы");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public static Type mapStringValueToType(String value) {
        if (value.equals(INCOME.getValue())) {
            return INCOME;
        }
        if (value.equals(EXPENSE.getValue())) {
            return EXPENSE;
        }
        throw new NullPointerException("Type not found");
    }
}
