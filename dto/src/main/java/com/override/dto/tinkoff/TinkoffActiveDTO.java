package com.override.dto.tinkoff;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TinkoffActiveDTO {
    private String name;
    private String ticker;
    private String figi;
    private Integer quantity;
    private Integer quantityLots;
    private BigDecimal currentPrice;
    private BigDecimal averagePositionPrice;
    private BigDecimal expectedYield;
}