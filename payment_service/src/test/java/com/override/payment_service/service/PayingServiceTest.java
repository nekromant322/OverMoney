package com.override.payment_service.service;

import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.config.PayingConfig;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.PaymentCallback;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.service.robokassa.RoboKassaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayingServiceTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RoboKassaService roboKassaService;

    @Mock
    private PayingConfig payingConfig;

    @InjectMocks
    private PayingService payingService;

    private final Long chatId = 123L;

    private Subscription subscription;
    private Payment payment;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setChatId(chatId);

        payment = Payment.builder()
                .invoiceId(1L)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(500))
                .currency(Currency.RUB)
                .build();
    }

    // ============================
    // createPayment
    // ============================

    @Test
    void createPayment_shouldCreatePaymentAndReturnUrl() {

        when(subscriptionService.getOrCreateSubscription(chatId))
                .thenReturn(subscription);

        when(payingConfig.getSubscriptionAmount())
                .thenReturn(BigDecimal.valueOf(500));

        when(paymentService.save(any(Payment.class)))
                .thenReturn(payment);

        when(roboKassaService.buildPaymentUrl(payment))
                .thenReturn("payment-url");

        String result = payingService.createPayment(chatId);

        assertEquals("payment-url", result);

        verify(subscriptionService).getOrCreateSubscription(chatId);
        verify(paymentService).save(any(Payment.class));
        verify(subscriptionService).attachPayment(subscription, payment);
        verify(roboKassaService).buildPaymentUrl(payment);
    }

    // ============================
    // handlePaymentCallback
    // ============================

    @Test
    void handlePaymentCallback_shouldActivateSubscriptionAndSendNotification() {

        PaymentCallback callback = new PaymentCallback();
        callback.setInvoiceId(1L);

        when(paymentService.successPayment(1L))
                .thenReturn(payment);

        when(subscriptionService.activateSubscription(payment))
                .thenReturn(subscription);

        String result = payingService.handlePaymentCallback(callback);

        assertEquals("OK1", result);

        verify(roboKassaService).validatePaymentCallbackSignature(callback);
        verify(paymentService).successPayment(1L);
        verify(subscriptionService).activateSubscription(payment);
    }
}