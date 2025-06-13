package com.override.payment_service.mapper;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.dto.constants.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class YooKassaMapper {

    public YooKassaRequestDTO mapToYooKassaRequest(PaymentRequestDTO request) {
        return YooKassaRequestDTO.builder()
                .capture(true)
                .description(request.getDescription())
                .amount(YooKassaRequestDTO.Amount.builder()
                        .value(request.getAmount().toPlainString())
                        .currency(request.getCurrency())
                        .build())
                .confirmation(YooKassaRequestDTO.Confirmation.builder()
                        .returnUrl(request.getReturnUrl())
                        .build())
                .build();
    }

    public PaymentResponseDTO mapToPaymentResponse(YooKassaResponseDTO yooKassaResponse,
                                                   PaymentRequestDTO paymentRequest) {
        return PaymentResponseDTO.builder()
                .orderId(paymentRequest.getOrderId())
                .paymentUrl(yooKassaResponse.getConfirmation().getConfirmationUrl())
                .status(PaymentStatus.PENDING)
                .build();
    }

    public String generateIdempotenceKey() {
        return UUID.randomUUID().toString();
    }
}