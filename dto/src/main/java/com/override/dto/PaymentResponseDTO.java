package com.override.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String paymentId;
    private String status;
    private String confirmationUrl;
    private String qrCodeUrl;
    private String orderId;
}