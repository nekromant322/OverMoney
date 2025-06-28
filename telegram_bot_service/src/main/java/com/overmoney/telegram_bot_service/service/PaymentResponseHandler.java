package com.overmoney.telegram_bot_service.service;

import com.override.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaymentResponseHandler {

    private final Map<String, String> paymentUrls = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> latches = new ConcurrentHashMap<>();

    private static final int PAYMENT_URL_TIMEOUT_SECONDS = 10;

    public void handlePaymentResponse(PaymentResponseDTO paymentResponse) {
        if (paymentResponse == null || paymentResponse.getOrderId() == null) {
            log.error("Получен null в handlePaymentResponse");
            return;
        }

        if (paymentResponse.getPaymentUrl() != null) {
            paymentUrls.put(paymentResponse.getOrderId(), paymentResponse.getPaymentUrl());
            CountDownLatch latch = latches.get(paymentResponse.getOrderId());
            if (latch != null) {
                latch.countDown();
            }
        }
        log.info("Обработка ответа на платеж: {}, статус: {}",
                paymentResponse.getOrderId(), paymentResponse.getStatus());
    }

    public String waitForPaymentUrl(String orderId) {
        CountDownLatch latch = new CountDownLatch(1);
        latches.put(orderId, latch);

        try {
            if (latch.await(PAYMENT_URL_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return paymentUrls.remove(orderId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ожидание paymentUrl прервано для orderId: {}", orderId);
        } finally {
            latches.remove(orderId);
        }
        return null;
    }
}