package com.override.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String orderId;
    private String paymentUrl;
    private String status;
}