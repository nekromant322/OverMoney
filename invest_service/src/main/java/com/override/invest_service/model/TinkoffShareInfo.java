package com.override.invest_service.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TinkoffShareInfo {
    private String name;
    private String ticker;
    private int lot; //можно покупать только кратно этой цифре
    private Double priceForOne;
    private Double priceForLot;
}
