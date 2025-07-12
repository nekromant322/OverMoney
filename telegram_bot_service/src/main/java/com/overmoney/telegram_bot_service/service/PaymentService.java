package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.PaymentFeign;
import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentFeign paymentFeign;
    private final PaymentResponseHandler paymentResponseHandler;
    private final KafkaSubscriptionProducerService kafkaProducerService;

    public String createPayment(PaymentRequestDTO request) {
        if ("true".equalsIgnoreCase(System.getProperty("kafka.enabled", "true"))) {
            String orderId = kafkaProducerService.sendPaymentRequest(request);
            return paymentResponseHandler.waitForPaymentUrl(orderId);
        } else {
            PaymentResponseDTO response = paymentFeign.createPayment(request);
            return response.getPaymentUrl();
        }
    }

    public SubscriptionDTO getSubscriptionStatus(Long chatId) {
        return paymentFeign.getSubscriptionStatus(chatId);
    }
}