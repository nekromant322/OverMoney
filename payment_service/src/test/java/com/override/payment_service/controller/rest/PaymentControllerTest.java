package com.override.payment_service.controller.rest;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.service.YooKassaService;
import com.override.payment_service.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private YooKassaService yooKassaService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    public void testCreatePayment() {
        PaymentRequestDTO request = TestUtil.createTestPaymentRequest();
        PaymentResponseDTO mockResponse = new PaymentResponseDTO();
        mockResponse.setPaymentUrl("https://yookassa.test/confirm");
        mockResponse.setOrderId(request.getOrderId());

        when(yooKassaService.createPayment(request)).thenReturn(mockResponse);

        PaymentResponseDTO response = paymentController.createPayment(request);

        assertNotNull(response);
        assertEquals(mockResponse.getPaymentUrl(), response.getPaymentUrl());
        assertEquals(request.getOrderId(), response.getOrderId());

        verify(yooKassaService, times(1)).createPayment(request);
    }
}