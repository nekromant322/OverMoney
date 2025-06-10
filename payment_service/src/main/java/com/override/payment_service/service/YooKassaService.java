package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.payment_service.client.YooKassaClient;
import com.override.payment_service.mapper.YooKassaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YooKassaService {

    private final YooKassaClient yooKassaClient;
    private final YooKassaMapper yooKassaMapper;

    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        YooKassaRequestDTO yooKassaRequest = yooKassaMapper.mapToYooKassaRequest(request);
        String idempotenceKey = yooKassaMapper.generateIdempotenceKey();

        YooKassaResponseDTO response = yooKassaClient.createPayment(
                idempotenceKey,
                yooKassaRequest
        );

        return yooKassaMapper.mapToPaymentResponse(response, request);
    }
}