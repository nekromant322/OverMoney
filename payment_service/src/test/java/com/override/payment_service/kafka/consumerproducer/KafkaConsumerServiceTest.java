package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.service.YooKassaService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

public class KafkaConsumerServiceTest {

    private static final String TEST_ORDER_ID = "order456";
    private static final String TEST_KEY = "key123";
    private static final String TEST_CORRELATION_ID = "correlationId123";
    private static final String TEST_PAYMENT_URL = "payment-url";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(200);

    @Test
    public void testListenForPaymentRequests_success() {
        YooKassaService yooKassaService = mock(YooKassaService.class);
        KafkaProducerService kafkaProducerService = mock(KafkaProducerService.class);
        KafkaConsumerService consumerService = new KafkaConsumerService(yooKassaService, kafkaProducerService);

        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("test")
                .returnUrl("url")
                .build();

        PaymentResponseDTO expectedResponse = PaymentResponseDTO.builder()
                .orderId(TEST_ORDER_ID)
                .paymentUrl(TEST_PAYMENT_URL)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(yooKassaService.createPayment(any(PaymentRequestDTO.class))).thenReturn(expectedResponse);

        consumerService.listenForPaymentRequests(request, TEST_KEY, TEST_CORRELATION_ID);

        verify(kafkaProducerService, times(1))
                .sendPaymentResponse(eq(TEST_KEY), eq(TEST_CORRELATION_ID), any(PaymentResponseDTO.class));
    }
}