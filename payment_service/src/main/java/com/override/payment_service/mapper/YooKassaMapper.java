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
        YooKassaRequestDTO yooKassaRequest = new YooKassaRequestDTO();

        YooKassaRequestDTO.Amount amount = new YooKassaRequestDTO.Amount();
        amount.setValue(request.getAmount().toPlainString());
        amount.setCurrency(request.getCurrency());
        yooKassaRequest.setAmount(amount);

        yooKassaRequest.setDescription(request.getDescription());

        YooKassaRequestDTO.Confirmation confirmation = new YooKassaRequestDTO.Confirmation();
        confirmation.setReturnUrl(request.getReturnUrl());
        yooKassaRequest.setConfirmation(confirmation);

        return yooKassaRequest;
    }

    public PaymentResponseDTO mapToPaymentResponse(
            YooKassaResponseDTO yooKassaResponse,
            PaymentRequestDTO paymentRequest
    ) {
        PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
        paymentResponse.setOrderId(paymentRequest.getOrderId());
        paymentResponse.setPaymentUrl(yooKassaResponse.getConfirmation().getConfirmationUrl());
        paymentResponse.setStatus(PaymentStatus.PENDING);
        return paymentResponse;
    }

    public String generateIdempotenceKey() {
        return UUID.randomUUID().toString();
    }
}