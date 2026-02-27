package com.override.dto;

import com.override.dto.constants.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Сумма транзакций по категории")
public class SumTransactionPerCategoryPerPeriodDTO {

    @Schema(description = "Id категории", example = "1")
    private Long id;

    @Schema(description = "Имя категории", example = "Рестораны")
    private String name;

    @Schema(description = "Сумма транзакций", example = "1200.0")
    private Double sum;

    @Schema(description = "Тип категории", example = "EXPENSE|INCOME")
    private Type type;
}
