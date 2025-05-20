package com.override.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PaymentRequestDTO {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Map<String, String> metadata;
    private boolean capture = true;
    private String confirmationType = "redirect"; // or "qr"
}