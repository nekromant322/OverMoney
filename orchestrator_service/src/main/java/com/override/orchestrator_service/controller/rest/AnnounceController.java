package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.service.TelegramBotRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/announce")
public class AnnounceController {

    @Autowired
    private TelegramBotRequestService telegramBotRequestService;

    @PostMapping("/send")
    public void sendAnnounce(@RequestBody String text) {
        telegramBotRequestService.sendAnnounce(text);
    }
}
