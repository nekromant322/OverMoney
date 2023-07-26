package com.override.orchestrator_service.controller.rest;

import com.override.dto.AnalyticsAnnualAndMonthlyReportDTO;
import com.override.dto.AnalyticsDataDTO;
import com.override.dto.AnalyticsDataMonthDTO;
import com.override.dto.AnalyticsMonthlyReportForYearDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.service.AnalyticService;
import com.override.orchestrator_service.service.OverMoneyAccountService;
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

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    @GetMapping("/totalCategorySums/{type}")
    public List<AnalyticsDataDTO> getAnalyticsTotalCategorySums(Principal principal, @PathVariable("type") Type type) throws InstanceNotFoundException {
        return analyticService.getTotalCategorySumsForAnalytics(telegramUtils.getTelegramId(principal), type);
    }

    @GetMapping("/totalIncomeOutcome/{year}")
    public List<AnalyticsDataMonthDTO> getIncomeOutcomePerMonth(Principal principal, @PathVariable("year") int year) throws InstanceNotFoundException {
        Long userId = telegramUtils.getTelegramId(principal);
        Long overMoneyAccountId = overMoneyAccountService.getAccountByUserId(userId).getId();
        return analyticService.getTotalIncomeOutcomePerMonth(overMoneyAccountId, year);
    }

    @GetMapping("/available-years")
    public List<Integer> getAvailableYears(Principal principal) throws InstanceNotFoundException {
        return analyticService.findAvailableYears(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/income/{year}")
    public List<AnalyticsMonthlyReportForYearDTO> getYearIncomeStatistics(Principal principal, @PathVariable("year") Integer year) throws InstanceNotFoundException {
        return analyticService.findMonthlyIncomeStatisticsForYearByAccountId(telegramUtils.getTelegramId(principal),
                year);
    }

    @GetMapping("/total/{year}")
    public List<AnalyticsAnnualAndMonthlyReportDTO> getYearTotalStatistics(Principal principal, @PathVariable("year") Integer year) throws InstanceNotFoundException {
        return analyticService.findAnnualAndMonthlyTotalStatisticsByAccountId(telegramUtils.getTelegramId(principal), year);
    }
}
