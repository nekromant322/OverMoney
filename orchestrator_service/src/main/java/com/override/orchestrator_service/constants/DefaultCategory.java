package com.override.orchestrator_service.constants;

import lombok.Getter;

@Getter
public enum DefaultCategory {

    SALARY("Зарплата", Type.INCOME),
    PRODUCTS("Продукты", Type.EXPENSE),
    TRANSPORT("Транспорт", Type.EXPENSE),
    RESTAURANTS("Рестораны", Type.EXPENSE);

    private final String name;
    private final Type type;

    DefaultCategory(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
