package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.service.SubscriptionService;
import com.override.payment_service.service.YooKassaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    private static final String TEST_ORDER_ID = "order456";
    private static final String TEST_KEY = "key123";
    private static final String TEST_CORRELATION_ID = "correlationId123";
    private static final String TEST_PAYMENT_URL = "payment-url";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(200);
    private static final String TEST_PAYMENT_ID = "payment123";

    @Mock
    private YooKassaService yooKassaService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private KafkaConsumerService consumerService;

    @Test
    public void testListenForPaymentRequests_success() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("test")
                .returnUrl("url")
                .build();

        PaymentResponseDTO yooKassaResponse = PaymentResponseDTO.builder()
                .paymentUrl(TEST_PAYMENT_URL)
                .build();

        when(yooKassaService.createPayment(any(PaymentRequestDTO.class))).thenReturn(yooKassaResponse);

        consumerService.listenForPaymentRequests(request, TEST_KEY, TEST_CORRELATION_ID);

        verify(yooKassaService).createPayment(request);
        verify(kafkaProducerService).sendPaymentResponse(
                eq(TEST_KEY),
                eq(TEST_CORRELATION_ID),
                argThat(response ->
                        response.getOrderId().equals(TEST_ORDER_ID) &&
                                response.getPaymentUrl().equals(TEST_PAYMENT_URL) &&
                                response.getStatus() == PaymentStatus.SUCCESS
                ));
    }

    @Test
    public void testListenForPaymentRequests_failure() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("test")
                .returnUrl("url")
                .build();

        when(yooKassaService.createPayment(any(PaymentRequestDTO.class)))
                .thenThrow(new RuntimeException("Payment error"));

        consumerService.listenForPaymentRequests(request, TEST_KEY, TEST_CORRELATION_ID);

        verify(kafkaProducerService).sendPaymentResponse(
                eq(TEST_KEY),
                eq(TEST_CORRELATION_ID),
                argThat(response ->
                        response.getOrderId().equals(TEST_ORDER_ID) &&
                                response.getPaymentUrl() == null &&
                                response.getStatus() == PaymentStatus.CANCELED
                ));
    }

    @Test
    public void testListenForSubscriptionStatusUpdates() {
        PaymentResponseDTO paymentResponse = PaymentResponseDTO.builder()
                .paymentId(TEST_PAYMENT_ID)
                .status(PaymentStatus.SUCCESS)
                .build();

        consumerService.listenForSubscriptionStatusUpdates(paymentResponse);

        verify(subscriptionService).updateSubscriptionStatus(
                TEST_PAYMENT_ID,
                PaymentStatus.SUCCESS);
    }
}