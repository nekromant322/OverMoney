package com.override.dto.constants;

import lombok.Getter;

@Getter
public enum StatusMailing {
    SUCCESS("Успешно"),
    ERROR("Ошибка"),
    PENDING("В ожидании");

    private final String value;

    StatusMailing(String value) {
        this.value = value;
    }
}
