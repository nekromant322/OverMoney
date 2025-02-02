package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.TelegramMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/telegram-message")
public class TelegramMessageController {
    @Autowired
    private TelegramMessageService telegramMessageService;

    @DeleteMapping("/ids")
    public void deleteTelegramMessageByIdTransaction(@RequestParam List<UUID> ids) {
        telegramMessageService.deleteTelegramMessageByIdTransactions(ids);
    }
}
