package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.MessageTelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageTelegramController {
    @Autowired
    private MessageTelegramService messageTelegramService;

    @DeleteMapping("/{id}")
    public void deleteMessageTelegramByIdTransaction(@PathVariable("id") UUID id) {
        messageTelegramService.deleteMessageTelegramByIdTransaction(id);
    }
}
