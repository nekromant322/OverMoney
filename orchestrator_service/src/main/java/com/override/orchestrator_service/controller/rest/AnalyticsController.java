package com.override.orchestrator_service.controller.rest;

import com.override.dto.*;
import com.override.dto.constants.Period;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.service.AnalyticService;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import com.override.orchestrator_service.util.TelegramUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<AnalyticsDataDTO> getAnalyticsTotalCategorySums(Principal principal, @PathVariable("type") Type type)
            throws InstanceNotFoundException {
        return analyticService.getTotalCategorySumsForAnalytics(telegramUtils.getTelegramId(principal), type);
    }

    @GetMapping("/totalIncomeOutcome/{year}")
    public List<AnalyticsDataMonthDTO> getIncomeOutcomePerMonth(Principal principal, @PathVariable("year") int year)
            throws InstanceNotFoundException {
        return analyticService.getTotalIncomeOutcomePerMonth(telegramUtils.getTelegramId(principal), year);
    }

    @GetMapping("/available-years")
    public List<Integer> getAvailableYears(Principal principal) throws InstanceNotFoundException {
        return analyticService.findAvailableYears(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/income/{year}")
    public List<AnalyticsMonthlyReportForYearDTO> getYearIncomeStatistics(Principal principal,
                                                                          @PathVariable("year") Integer year)
            throws InstanceNotFoundException {
        return analyticService.findMonthlyIncomeStatisticsForYearByAccountId(telegramUtils.getTelegramId(principal),
                year);
    }

    @GetMapping("/total/{year}")
    public List<AnalyticsAnnualAndMonthlyReportDTO> getYearTotalStatistics(Principal principal,
                                                                           @PathVariable("year") Integer year)
            throws InstanceNotFoundException {
        return analyticService.findAnnualAndMonthlyTotalStatisticsByAccountId(telegramUtils.getTelegramId(principal),
                year);
    }

    @GetMapping("/categories/sums")
    @Operation(summary = "Получить суммы трат и доходов пользователя по категориям",
            description = "Возвращает список категорий с указанием суммы потраченной или полученной для категории" +
                    "за период день/месяц/год")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список трат/доходов получен")
    })
    List<SumTransactionPerCategoryPerPeriodDTO> getCategoriesWithSumOfTransactionsPerPeriod(
            Principal principal,
            @Parameter(description = "Период для выборки YEAR|MONTH|DAY")
            @RequestParam(defaultValue = "DAY") Period period
    ) throws InstanceNotFoundException {
        return analyticService.getUserCategoriesWithSumOfTransactionsPerPeriod(
                telegramUtils.getTelegramId(principal),
                period
        );
    }
}
