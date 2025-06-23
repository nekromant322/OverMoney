package com.override.dto;

import com.override.dto.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDTO {
    private String orderId;
    private String paymentUrl;
    private PaymentStatus status;
    private String paymentId;
}