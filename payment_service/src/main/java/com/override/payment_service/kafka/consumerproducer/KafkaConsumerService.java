package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.service.YooKassaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final YooKassaService yooKassaService;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "payment-requests", groupId = "payment-service-group")
    public void listenForPaymentRequests(
            @Payload PaymentRequestDTO paymentRequest,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {

        log.info("Получен запрос на оплату заказа: {}", paymentRequest.getOrderId());

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(paymentRequest.getOrderId());

        try {
            PaymentResponseDTO yooKassaResponse = yooKassaService.createPayment(paymentRequest);
            response.setPaymentUrl(yooKassaResponse.getPaymentUrl());
            response.setStatus("success");
            log.info("Платеж успешно создан для заказа: {}", paymentRequest.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка обработки платежа за заказ: {}", paymentRequest.getOrderId(), e);
            response.setStatus("failed");
            response.setPaymentUrl(null);
        }

        kafkaProducerService.sendPaymentResponse(key, correlationId, response);
    }
}