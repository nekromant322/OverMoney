package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.client.YooKassaClient;
import com.override.payment_service.mapper.YooKassaMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

public class YooKassaServiceTest {

    @Test
    public void testCreatePayment_success() {
        YooKassaClient mockClient = mock(YooKassaClient.class);
        YooKassaMapper mockMapper = mock(YooKassaMapper.class);
        YooKassaService service = new YooKassaService(mockClient, mockMapper);

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId("order123");
        request.setAmount(BigDecimal.valueOf(100));
        request.setCurrency(Currency.RUB);
        request.setDescription("Test payment");
        request.setReturnUrl("https://example.com/success");

        YooKassaRequestDTO mappedRequest = new YooKassaRequestDTO();
        when(mockMapper.mapToYooKassaRequest(request)).thenReturn(mappedRequest);
        when(mockMapper.generateIdempotenceKey()).thenReturn("test-key");

        YooKassaResponseDTO.Confirmation confirmation = new YooKassaResponseDTO.Confirmation();
        confirmation.setConfirmationUrl("https://payment.com/confirm");

        YooKassaResponseDTO yooKassaResponse = new YooKassaResponseDTO();
        yooKassaResponse.setStatus(PaymentStatus.PENDING);
        yooKassaResponse.setConfirmation(confirmation);

        when(mockClient.createPayment(eq("test-key"), eq(mappedRequest)))
                .thenReturn(yooKassaResponse);

        PaymentResponseDTO expectedResponse = new PaymentResponseDTO();
        expectedResponse.setOrderId("order123");
        expectedResponse.setPaymentUrl("https://payment.com/confirm");
        expectedResponse.setStatus(PaymentStatus.PENDING);

        when(mockMapper.mapToPaymentResponse(yooKassaResponse, request))
                .thenReturn(expectedResponse);

        PaymentResponseDTO result = service.createPayment(request);

        assertThat(result)
                .hasFieldOrPropertyWithValue("orderId", "order123")
                .hasFieldOrPropertyWithValue("paymentUrl", "https://payment.com/confirm")
                .hasFieldOrPropertyWithValue("status", PaymentStatus.PENDING);

        verify(mockMapper).mapToYooKassaRequest(request);
        verify(mockMapper).generateIdempotenceKey();
        verify(mockClient).createPayment("test-key", mappedRequest);
        verify(mockMapper).mapToPaymentResponse(yooKassaResponse, request);
    }
}