package com.override.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String returnUrl;
    private String description;
}