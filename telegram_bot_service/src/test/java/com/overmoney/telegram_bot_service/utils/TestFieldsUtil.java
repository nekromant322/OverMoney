package com.overmoney.telegram_bot_service.utils;

import com.override.dto.TransactionMessageDTO;
import org.telegram.telegrambots.meta.api.objects.File;

public class TestFieldsUtil {

    public static TransactionMessageDTO generateTransactionDTO() {
        return TransactionMessageDTO.builder()
                .message("message")
                .username("username")
                .build();
    }

    public static File generateTelegramFile() {
        return new File("", "", 0L, "path");
    }
}
