package com.override.dto.constants;

public enum StatusMailing {
    SUCCESSFULLY("Успешно"),
    ERROR("Ошибка"),
    AWAIT_SENDING("Ожидание отправки");

    private final String value;

    StatusMailing(String value) {
        this.value = value;
    }
}
