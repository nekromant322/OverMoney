package com.overmoney.telegram_bot_service.service;

import com.override.dto.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PaymentResponseHandlerTest {

    private PaymentResponseHandler responseHandler;

    @BeforeEach
    void setUp() {
        responseHandler = new PaymentResponseHandler();
    }

    @Test
    void waitForPaymentUrl_ShouldTimeout() throws InterruptedException {
        String orderId = "test-order";

        String result = responseHandler.waitForPaymentUrl(orderId);

        assertNull(result);
    }

    @Test
    void handlePaymentResponse_NullResponse_ShouldNotFail() {
        assertDoesNotThrow(() -> responseHandler.handlePaymentResponse(null));
    }

    @Test
    void handlePaymentResponse_NullOrderId_ShouldNotFail() {
        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .orderId(null)
                .paymentUrl("https://payment.url")
                .build();

        assertDoesNotThrow(() -> responseHandler.handlePaymentResponse(response));
    }
}