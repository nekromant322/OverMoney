package com.overmoney.telegram_bot_service.service;

import com.override.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class PaymentResponseHandler {

    private final Map<String, CompletableFuture<PaymentResponseDTO>> pendingPayments = new ConcurrentHashMap<>();

    private static final int PAYMENT_URL_TIMEOUT_SECONDS = 10;

    public void handlePaymentResponse(PaymentResponseDTO paymentResponse) {
        CompletableFuture<PaymentResponseDTO> future = pendingPayments.remove(paymentResponse.getOrderId());
        if (future != null) {
            future.complete(paymentResponse);
        }
    }

    public String waitForPaymentUrl(String orderId) {
        CompletableFuture<PaymentResponseDTO> future = new CompletableFuture<>();
        pendingPayments.put(orderId, future);

        try {
            PaymentResponseDTO response = future.get(PAYMENT_URL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return response.getPaymentUrl();
        } catch (Exception e) {
            pendingPayments.remove(orderId);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }
}