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
        // Конвертируем PaymentRequest в формат YooKassa
        YooKassaRequestDTO yooKassaRequest = new YooKassaRequestDTO();

        // Установить сумму
        YooKassaRequestDTO.Amount amount = new YooKassaRequestDTO.Amount();
        amount.setValue(request.getAmount().toPlainString()); // Используем toPlainString()
        amount.setCurrency(request.getCurrency());
        yooKassaRequest.setAmount(amount);

        // Установить описание
        yooKassaRequest.setDescription(request.getDescription());

        // Установить подтверждение
        YooKassaRequestDTO.Confirmation confirmation = new YooKassaRequestDTO.Confirmation();
        confirmation.setReturnUrl(request.getReturnUrl());
        yooKassaRequest.setConfirmation(confirmation);

        // Генерируем уникальный ключ идемпотентности
        String idempotenceKey = UUID.randomUUID().toString();

        // Вызываем YooKassa API (правильный вызов с одним заголовком)
        YooKassaResponseDTO response = yooKassaClient.createPayment(
                idempotenceKey,
                yooKassaRequest
        );

        // Конвертируем ответ в наш формат
        PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
        paymentResponse.setPaymentUrl(response.getConfirmation().getConfirmationUrl());

        return paymentResponse;
    }
}