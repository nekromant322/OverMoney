package com.override.dto.tinkoff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinkoffActiveMOEXDTO {
    private TinkoffActiveDTO tinkoffActiveDTO;
    private Double currentTotalPrice;
    private Double moexWeight;
    private Double currentWeight;
    /**
     * Насколько выполнена цель по акции для следования индексу мосбиржи
     */
    private Double percentFollowage;
    private Integer correctQuantity;
    private Integer quantityToBuy;
    private Integer lot;
}
