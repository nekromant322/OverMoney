package com.overmoney.telegram_bot_service.service;

import com.override.dto.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentResponseHandlerTest {

    private PaymentResponseHandler responseHandler;
    private static final String TEST_ORDER_ID = "test-order-123";
    private static final String TEST_PAYMENT_URL = "https://payment.url";

    @BeforeEach
    void setUp() {
        responseHandler = new PaymentResponseHandler();
    }

    @Test
    void waitForPaymentUrl_ShouldReturnNullWhenTimeout() {
        String result = responseHandler.waitForPaymentUrl(TEST_ORDER_ID);
        assertNull(result, "Должен вернуть null при таймауте");
    }

    @Test
    void handlePaymentResponse_ShouldCompleteFutureWhenResponseReceived() throws Exception {
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                PaymentResponseDTO response = PaymentResponseDTO.builder()
                        .orderId(TEST_ORDER_ID)
                        .paymentUrl(TEST_PAYMENT_URL)
                        .build();
                responseHandler.handlePaymentResponse(response);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        String result = responseHandler.waitForPaymentUrl(TEST_ORDER_ID);
        assertEquals(TEST_PAYMENT_URL, result, "Должен вернуть корректный URL платежа");
    }

    @Test
    void handlePaymentResponse_ShouldIgnoreUnrelatedOrderIds() {
        responseHandler.waitForPaymentUrl(TEST_ORDER_ID);

        PaymentResponseDTO unrelatedResponse = PaymentResponseDTO.builder()
                .orderId("other-order")
                .paymentUrl(TEST_PAYMENT_URL)
                .build();

        assertDoesNotThrow(() -> responseHandler.handlePaymentResponse(unrelatedResponse),
                "Должен игнорировать ответы с другими orderId");
    }
}