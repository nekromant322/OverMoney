package com.override.payment_service;

import com.override.dto.*;

import java.math.BigDecimal;
import java.util.UUID;

public class TestUtil {

    public static PaymentRequestDTO createTestPaymentRequest() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId("test-order-123");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("RUB");
        request.setReturnUrl("https://example.com/return");
        request.setDescription("Test payment");
        return request;
    }

    public static YooKassaResponseDTO createTestYooKassaResponse() {
        YooKassaResponseDTO response = new YooKassaResponseDTO();
        response.setId("test-payment-id");
        response.setStatus("pending");

        YooKassaResponseDTO.Confirmation confirmation = new YooKassaResponseDTO.Confirmation();
        confirmation.setConfirmationUrl("https://yookassa.test/confirm");
        response.setConfirmation(confirmation);

        return response;
    }
}