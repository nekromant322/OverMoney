package com.override.orchestrator_service.controller.rest;

import com.override.dto.AnalyticsDataMonthDiffDTO;
import com.override.dto.AnalyticsDataPerMonthDTO;
import com.override.dto.AnalyticsMainDataPerYearsDTO;
import com.override.dto.TransactionSummaryDTO;
import com.override.orchestrator_service.service.AnalyticV2Service;
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

@RestController
@RequestMapping("/analytics/v2")
@Slf4j
public class AnalyticsV2Controller {
    @Autowired
    private AnalyticV2Service analyticV2Service;
    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping("/general/amounts")
    @Operation(summary = "Получить финансовые показатели месяца", description =
            "Получить сведения по конкретным доходам" +
                    " и расходам текущего месяца")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public TransactionSummaryDTO getFinanceData(Principal principal) {
        return analyticV2Service.countFinanceData(telegramUtils.getTelegramId(principal));
    }

    @PostMapping("/months/amounts")
    @Operation(summary = "Получить месячные финансовые показатели", description =
            "Получить сумму доходов и расходов по " +
                    "каждому месяцу в рамках выбранного года")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public AnalyticsDataPerMonthDTO getFinanceDataPerMonth(
            Principal principal,
            @Parameter(description = "ID пользователя") @RequestParam(required = false) Long userId,
            @Parameter(description = "Выбранный год") @RequestParam int year) throws InstanceNotFoundException {
        return analyticV2Service.countFinanceDataPerMonth(telegramUtils.getTelegramId(principal), userId, year);
    }

    @GetMapping("/years/amounts")
    @Operation(summary = "Получить финансовые показатели за все года", description = "Получить сумму доходов и " +
            "расходов по каждому году для каждой категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public AnalyticsMainDataPerYearsDTO getFinancePerYear(Principal principal) throws InstanceNotFoundException {
        return analyticV2Service.countFinanceDataPerYear(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/delta")
    @Operation(summary = "Получить разницу в процентах относительно текущего месяца",
            description = "Получить разницу доходов и расходов в процентах относительно предыдущего месяца и " +
                    "относительно одноименного месяца предыдущего года")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public AnalyticsDataMonthDiffDTO getThisMonthDelta(Principal principal) {
        return analyticV2Service.getMonthDiff(telegramUtils.getTelegramId(principal));
    }
}