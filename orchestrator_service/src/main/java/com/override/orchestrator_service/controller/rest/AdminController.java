package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.service.TelegramBotRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TelegramBotRequestService telegramBotRequestService;

    @PostMapping("/announce")
    public void sendAnnounce(@RequestBody String text) {
        telegramBotRequestService.sendAnnounce(text);
    }
}
