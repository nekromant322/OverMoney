package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Данные о разнице доходов и расходов текущего месяца " +
        "с предыдущим и одноименным месяцем прошлого года")
public class AnalyticsDataMonthDiffDTO {

    @Schema(description = "Разница в доходах текущего месяца и предыдущего, %. " +
            "Положительное значение - доходы текущего месяца возросли относительно предыдущего месяца, " +
            "отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предыдущий период - null", nullable = true)
    private Integer currentMonthIncomeToPrevMonthDiff;

    @Schema(description = "Разница в расходах текущего месяца и предыдущего, %. " +
            "Положительное значение - расходы текущего месяца возросли относительно предыдущего месяца, " +
            "отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предыдущий период - null", nullable = true)
    private Integer currentMonthExpenseToPrevMonthDiff;

    @Schema(description = "Разница в расходах текущего месяца и одноименного месяца предыдущего года, %. " +
            "Положительное значение - расходы текущего месяца возросли относительно " +
            "одноименного месяца предыдущего года, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предыдущий период - null", nullable = true)
    private Integer currentMonthIncomeToPrevYearDiff;

    @Schema(description = "Разница в расходах текущего месяца и одноименного месяца предыдущего года, %. " +
            "Положительное значение - расходы текущего месяца возросли относительно " +
            "одноименного месяца предыдущего года, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предыдущий период - null", nullable = true)
    private Integer currentMonthExpenseToPrevYearDiff;

    @Schema(description = "Название текущего месяца")
    private String currentMonthName;

    @Schema(description = "Названия предыдущего месяца")
    private String previousMonthName;
}