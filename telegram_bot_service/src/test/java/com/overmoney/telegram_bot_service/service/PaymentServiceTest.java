package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import com.override.dto.PaymentRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private KafkaSubscriptionProducerService kafkaProducerService;

    @Mock
    private PaymentResponseHandler responseHandler;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_Success() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId("test-order")
                .amount(new BigDecimal("1000.00"))
                .build();

        when(kafkaProducerService.sendPaymentRequest(request))
                .thenReturn("test-order");
        when(responseHandler.waitForPaymentUrl("test-order"))
                .thenReturn("https://payment.url");

        String paymentUrl = paymentService.createPayment(request);

        assertEquals("https://payment.url", paymentUrl);
        verify(kafkaProducerService).sendPaymentRequest(request);
        verify(responseHandler).waitForPaymentUrl("test-order");
    }

    @Test
    void createPayment_Timeout() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId("test-order")
                .build();

        when(kafkaProducerService.sendPaymentRequest(request))
                .thenReturn("test-order");
        when(responseHandler.waitForPaymentUrl("test-order"))
                .thenReturn(null);

        String paymentUrl = paymentService.createPayment(request);

        assertNull(paymentUrl);
    }
}