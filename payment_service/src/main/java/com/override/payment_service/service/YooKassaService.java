package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.payment_service.client.YooKassaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class YooKassaService {

    private final YooKassaClient yooKassaClient;

    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        YooKassaRequestDTO yooKassaRequest = new YooKassaRequestDTO();

        YooKassaRequestDTO.Amount amount = new YooKassaRequestDTO.Amount();
        amount.setValue(request.getAmount().toPlainString());
        amount.setCurrency(request.getCurrency());
        yooKassaRequest.setAmount(amount);

        yooKassaRequest.setDescription(request.getDescription());

        YooKassaRequestDTO.Confirmation confirmation = new YooKassaRequestDTO.Confirmation();
        confirmation.setReturnUrl(request.getReturnUrl());
        yooKassaRequest.setConfirmation(confirmation);

        String idempotenceKey = UUID.randomUUID().toString();

        YooKassaResponseDTO response = yooKassaClient.createPayment(
                idempotenceKey,
                yooKassaRequest
        );

        PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
        paymentResponse.setPaymentUrl(response.getConfirmation().getConfirmationUrl());

        return paymentResponse;
    }
}