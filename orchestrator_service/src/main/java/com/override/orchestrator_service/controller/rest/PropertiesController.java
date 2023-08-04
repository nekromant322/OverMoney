package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.config.LongPollingOverviewProperties;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PropertiesController {

    @Autowired
    private LongPollingOverviewProperties longPollingOverviewProperties;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/longPolling")
    public Integer getLongPollingData() {
        return longPollingOverviewProperties.getPeriodOfInactivity();
    }

    @GetMapping("/telegramBotName")
    public String getTelegramBotName() {
        return telegramUtils.getTelegramBotName();
    }
}
