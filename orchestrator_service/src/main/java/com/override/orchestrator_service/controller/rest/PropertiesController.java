package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.config.LongPollingOvermoneyProperties;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PropertiesController {

    @Autowired
    private LongPollingOvermoneyProperties longPollingOvermoneyProperties;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/longPolling")
    public Integer getLongPollingData() {
        return longPollingOvermoneyProperties.getPeriodOfInactivity();
    }

    @GetMapping("/telegramBotName")
    public String getTelegramBotName() {
        return telegramUtils.getTelegramBotName();
    }
}
