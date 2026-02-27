package com.overmoney.telegram_bot_service.utils;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import com.override.dto.TransactionMessageDTO;
import org.telegram.telegrambots.meta.api.objects.File;

import java.util.UUID;

public class TestFieldsUtil {

    public static TransactionMessageDTO generateTransactionDTO() {
        return TransactionMessageDTO.builder()
                .message("message")
                .userId(123L)
                .build();
    }

    public static TelegramMessage generateTelegramMessage() {
        return new TelegramMessage(1L, 1, 1L, UUID.randomUUID());
    }

    public static File generateTelegramFile() {
        return new File("", "", 0L, "path");
    }
}
