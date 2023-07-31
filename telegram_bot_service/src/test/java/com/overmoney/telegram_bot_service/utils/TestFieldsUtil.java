package com.overmoney.telegram_bot_service.utils;

import com.overmoney.telegram_bot_service.model.MessageTelegram;
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

    public static MessageTelegram generateMessageTelegram() {
        return new MessageTelegram(1, UUID.randomUUID());
    }

    public static File generateTelegramFile() {
        return new File("", "", 0L, "path");
    }
}
