package com.override.dto.tinkoff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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