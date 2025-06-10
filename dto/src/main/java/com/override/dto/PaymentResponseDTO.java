package com.override.dto;

import com.override.dto.constants.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String orderId;
    private String paymentUrl;
    private PaymentStatus status;
}