package com.override.payment_service.service;

import com.override.dto.*;
import com.override.payment_service.client.YooKassaClient;
import com.override.payment_service.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class YooKassaServiceTest {

    @Mock
    private YooKassaClient yooKassaClient;

    @InjectMocks
    private YooKassaService yooKassaService;

    @Test
    public void testCreatePayment() {
        PaymentRequestDTO request = TestUtil.createTestPaymentRequest();
        YooKassaResponseDTO mockResponse = TestUtil.createTestYooKassaResponse();

        when(yooKassaClient.createPayment(anyString(), any(YooKassaRequestDTO.class)))
                .thenReturn(mockResponse);

        PaymentResponseDTO response = yooKassaService.createPayment(request);

        assertNotNull(response);
        assertEquals(mockResponse.getConfirmation().getConfirmationUrl(), response.getPaymentUrl());

        verify(yooKassaClient, times(1))
                .createPayment(anyString(), any(YooKassaRequestDTO.class));
    }
}