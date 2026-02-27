package com.override.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Данные о разнице доходов и расходов сравниваемого месяца " +
        "с месяцем, предшествующим сравниваемому, и одноименным сравниваемому месяцем прошлого года")
public class AnalyticsDataMonthDiffDTO {

    @Schema(description = "Разница в доходах сравниваемого месяца и месяца, предшествующего сравниваемому, %. " +
            "Положительное значение - доходы сравниваемого месяца возросли относительно месяца, предшествующего " +
            "сравниваемому, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предшествующий период - null", nullable = true)
    private Integer baseMonthIncomeToPrevMonthDiff;

    @Schema(description = "Разница в расходах сравниваемого месяца и месяца, предшествующего сравниваемому, %. " +
            "Положительное значение - расходы сравниваемого месяца возросли относительно месяца, предшествующего " +
            "сравниваемому, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предшествующий период - null", nullable = true)
    private Integer baseMonthExpenseToPrevMonthDiff;

    @Schema(description = "Разница в расходах сравниваемого месяца и одноименного ему месяца предыдущего года, %. " +
            "Положительное значение - расходы сравниваемого месяца возросли относительно " +
            "одноименного ему месяца предыдущего года, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предшествующий период - null", nullable = true)
    private Integer baseMonthIncomeToPrevYearDiff;

    @Schema(description = "Разница в расходах сравниваемого месяца и одноименного ему месяца предыдущего года, %. " +
            "Положительное значение - расходы сравниваемого месяца возросли относительно " +
            "одноименного ему месяца предыдущего года, отрицательное - снизились. " +
            "В случае отсутствия основания расчета - данных за предшествующий период - null", nullable = true)
    private Integer baseMonthExpenseToPrevYearDiff;

    @Schema(description = "Название сравниваемого месяца")
    private String baseMonthName;

    @Schema(description = "Названия месяца, предшествующего сравниваемому")
    private String previousMonthName;
}