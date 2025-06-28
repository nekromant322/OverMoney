package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import com.override.dto.PaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final KafkaSubscriptionProducerService kafkaProducerService;
    private final PaymentResponseHandler paymentResponseHandler;

    public String createPayment(PaymentRequestDTO request) {
        String orderId = kafkaProducerService.sendPaymentRequest(request);
        return paymentResponseHandler.waitForPaymentUrl(orderId);
    }
}