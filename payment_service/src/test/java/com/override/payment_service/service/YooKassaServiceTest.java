package com.override.payment_service.service;

import com.override.dto.*;
import com.override.payment_service.client.YooKassaClient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class YooKassaServiceTest {

    @Test
    public void testCreatePayment_success() {
        YooKassaClient mockClient = mock(YooKassaClient.class);
        YooKassaService service = new YooKassaService(mockClient);

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId("order123");
        request.setAmount(BigDecimal.valueOf(100));
        request.setCurrency("RUB");
        request.setDescription("Test payment");
        request.setReturnUrl("https://example.com/success");

        YooKassaResponseDTO.Confirmation confirmation = new YooKassaResponseDTO.Confirmation();
        confirmation.setConfirmationUrl("https://payment.com/confirm");

        YooKassaResponseDTO response = new YooKassaResponseDTO();
        response.setStatus("pending");
        response.setConfirmation(confirmation);

        when(mockClient.createPayment(any(), any())).thenReturn(response);

        PaymentResponseDTO result = service.createPayment(request);

        assertEquals("order123", result.getOrderId());
        assertEquals("https://payment.com/confirm", result.getPaymentUrl());
        assertEquals("pending", result.getStatus());
    }
}
