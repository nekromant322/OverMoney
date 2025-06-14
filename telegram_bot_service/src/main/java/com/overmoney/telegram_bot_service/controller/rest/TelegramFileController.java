package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.TelegramFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/telegram-files")
public class TelegramFileController {
    @Autowired
    TelegramFileService telegramFileService;

    @GetMapping("/profile-photo/{userId}")
    public String getPhoto(@PathVariable Long userId) throws IOException {
        return telegramFileService.getUserProfilePhotoBase64Format(userId);
    }
}
