package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionSummaryDTO;
import com.override.orchestrator_service.service.AnalyticV2Service;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/analytics/v2")
@Slf4j
public class AnalyticsV2Controller {
    @Autowired
    private AnalyticV2Service analyticV2Service;
    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/general/amounts")
    public TransactionSummaryDTO getFinanceData(Principal principal) {
        return analyticV2Service.countFinanceData(telegramUtils.getTelegramId(principal));
    }
}
