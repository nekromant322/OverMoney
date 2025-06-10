package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.constants.KafkaConstants;
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

    @KafkaListener(
            topics = KafkaConstants.PAYMENT_REQUESTS_TOPIC,
            groupId = KafkaConstants.PAYMENT_SERVICE_GROUP
    )
    public void listenForPaymentRequests(
            @Payload PaymentRequestDTO paymentRequest,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {

        String orderId = paymentRequest.getOrderId();
        log.info("Получен запрос на оплату заказа: {}", orderId);

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(paymentRequest.getOrderId());

        try {
            PaymentResponseDTO yooKassaResponse = yooKassaService.createPayment(paymentRequest);
            response.setPaymentUrl(yooKassaResponse.getPaymentUrl());
            response.setStatus(PaymentStatus.SUCCESS);
            log.info("Платеж успешно создан для заказа: {}", orderId);
        } catch (Exception e) {
            log.error("Ошибка обработки платежа за заказ: {}", orderId, e);
            response.setStatus(PaymentStatus.FAILED);
            response.setPaymentUrl(null);
        }

        kafkaProducerService.sendPaymentResponse(key, correlationId, response);
    }
}