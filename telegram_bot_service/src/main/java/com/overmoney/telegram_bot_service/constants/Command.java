package com.overmoney.telegram_bot_service.constants;

import lombok.Getter;

@Getter
public enum Command {

    START("/start", "OverMoney - бот для учета финансов!"),
    MONEY("/money", "Приложение OverMoney");

    private final String alias;
    private final String description;

    Command(String alias, String description) {
        this.alias = alias;
        this.description = description;
    }
}
