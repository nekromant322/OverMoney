package com.overmoney.telegram_bot_service.utils;

import com.overmoney.telegram_bot_service.model.TransactionDTO;
import org.telegram.telegrambots.meta.api.objects.File;

public class TestFieldsUtil {

    public static TransactionDTO generateTransactionDTO() {
        return TransactionDTO.builder()
                .message("message")
                .username("username")
                .build();
    }

    public static File generateTelegramFile() {
        return new File("", "", 0L, "path");
    }
}
