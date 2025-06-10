package com.override.dto;

import com.override.dto.constants.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String orderId;
    private BigDecimal amount;
    private Currency currency;
    private String returnUrl;
    private String description;
}