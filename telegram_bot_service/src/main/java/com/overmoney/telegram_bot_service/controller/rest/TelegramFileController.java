package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.TelegramFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram-files")
public class TelegramFileController {
    @Autowired
    private TelegramFileService telegramFileService;

    @GetMapping("user/{userId}/photo")
    public byte[] getPhoto(@PathVariable Long userId) {
        return telegramFileService
                .getUserProfilePhoto(userId)
                .orElse(null);
    }
}
