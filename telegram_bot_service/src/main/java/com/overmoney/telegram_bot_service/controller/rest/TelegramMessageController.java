package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.TelegramMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/telegram-message")
public class TelegramMessageController {
    @Autowired
    private TelegramMessageService telegramMessageService;

    @DeleteMapping("/{id}")
    public void deleteTelegramMessageByIdTransaction(@PathVariable("id") UUID id) {
        telegramMessageService.deleteTelegramMessageByIdTransaction(id);
    }
}
