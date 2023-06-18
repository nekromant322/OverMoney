package com.overmoney.telegram_bot_service.constants;

import lombok.Getter;

@Getter
public enum InlineKeyboardCallback {
    DEFAULT("Нет", "default"),
    MERGE_CATEGORIES("Перенести категории", "mergeWithCategories"),
    MERGE_CATEGORIES_AND_TRANSACTIONS("Перенести категории и транзакции", "mergeWithCategoriesAndTransactions");

    private final String text;
    private final String data;

    InlineKeyboardCallback(String text, String data) {
        this.text = text;
        this.data = data;
    }
}
