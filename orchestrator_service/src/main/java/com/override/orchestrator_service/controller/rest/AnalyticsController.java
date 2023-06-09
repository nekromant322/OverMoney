package com.override.orchestrator_service.controller.rest;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.service.AnalyticService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired
    private AnalyticService analyticService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/totalCategorySums/{type}")
    public List<AnalyticsDataDTO> getAnalyticsTotalCategorySums(Principal principal, @PathVariable("type") Type type) throws InstanceNotFoundException {
        return analyticService.getTotalCategorySumsForAnalytics(telegramUtils.getTelegramId(principal), type);
    }
}
