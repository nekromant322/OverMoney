package com.override.orchestrator_service.filter;

import com.override.dto.AmountRangeDTO;
import com.override.dto.DateRangeDTO;
import com.override.orchestrator_service.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "Набор фильтров для получения транзакций")
public class TransactionFilter {

    //todo private List<String> categories??
    private Category category;

    @Schema(description = "Диапазон суммы транзакции", nullable = true)
    private AmountRangeDTO amount;

    @Schema(description = "Фильтр по тексту комментария", nullable = true, example = "кофе")
    private String message;

    @Schema(description = "Диапазон дат транзакции", nullable = true)
    private DateRangeDTO date;

    @Schema(description = "Список Telegram user ID", nullable = true, example = "[1026133149]")
    private List<Long> telegramUserIdList;

    @Schema(description = "Размер страницы (пагинация)", example = "25")
    private Integer pageSize;

    @Schema(description = "Номер страницы (пагинация)", example = "1")
    private Integer pageNumber;
}