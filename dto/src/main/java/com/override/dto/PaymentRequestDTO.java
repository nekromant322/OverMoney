package com.override.dto;

import com.override.dto.constants.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Long chatId;
    private String orderId;
    private BigDecimal amount;
    private Currency currency;
    private String returnUrl;
    private String description;
}