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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    private static final String TEST_ORDER_ID = "order-789";
    private static final String TEST_KEY = "key-456";
    private static final String TEST_CORRELATION_ID = "corr-123";
    private static final String TEST_PAYMENT_URL = "https://payment.url";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(200);
    private static final String TEST_PAYMENT_ID = "payment-789";
    private static final Long TEST_CHAT_ID = 67890L;

    @Mock
    private YooKassaService yooKassaService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private KafkaConsumerService consumerService;

    @Test
    public void listenForPaymentRequests_ShouldProcessSuccessfully() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("Test payment")
                .returnUrl("https://return.url")
                .chatId(TEST_CHAT_ID)
                .build();

        PaymentResponseDTO yooKassaResponse = PaymentResponseDTO.builder()
                .orderId(TEST_ORDER_ID)
                .paymentUrl(TEST_PAYMENT_URL)
                .status(PaymentStatus.PENDING)
                .build();

        when(subscriptionService.createOrGetExistingPayment(eq(TEST_CHAT_ID), any(PaymentRequestDTO.class)))
                .thenReturn(yooKassaResponse);

        consumerService.listenForPaymentRequests(request, TEST_KEY, TEST_CORRELATION_ID);

        verify(kafkaProducerService).sendPaymentResponse(
                eq(TEST_KEY),
                eq(TEST_CORRELATION_ID),
                argThat(response ->
                        response.getOrderId().equals(TEST_ORDER_ID) &&
                                response.getPaymentUrl().equals(TEST_PAYMENT_URL) &&
                                response.getStatus() == PaymentStatus.PENDING
                ));
    }

    @Test
    public void listenForPaymentRequests_ShouldHandleFailure() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("Test payment")
                .returnUrl("https://return.url")
                .chatId(TEST_CHAT_ID)
                .build();

        when(subscriptionService.createOrGetExistingPayment(eq(TEST_CHAT_ID), any(PaymentRequestDTO.class)))
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
    public void listenForSubscriptionStatusUpdates_ShouldUpdateStatus() {
        PaymentResponseDTO paymentResponse = PaymentResponseDTO.builder()
                .paymentId(TEST_PAYMENT_ID)
                .status(PaymentStatus.SUCCESS)
                .build();

        consumerService.listenForSubscriptionStatusUpdates(paymentResponse);

        verify(subscriptionService).updateSubscriptionStatus(TEST_PAYMENT_ID, PaymentStatus.SUCCESS);
    }
}