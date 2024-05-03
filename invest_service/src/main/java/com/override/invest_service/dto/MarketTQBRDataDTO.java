package com.override.invest_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTQBRDataDTO {
    private String ticker;
    private double price;
    private int lots;
}
