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

        log.info("Received payment request for order: {}", paymentRequest.getOrderId());

        try {
            PaymentResponseDTO response = yooKassaService.createPayment(paymentRequest);
            log.info("Payment created successfully for order: {}", paymentRequest.getOrderId());

            // Send response back via Kafka
            kafkaProducerService.sendPaymentResponse(key, correlationId, response);
        } catch (Exception e) {
            log.error("Failed to create payment for order: {}", paymentRequest.getOrderId(), e);

            // Send error response
            PaymentResponseDTO errorResponse = new PaymentResponseDTO();
            errorResponse.setOrderId(paymentRequest.getOrderId());
            errorResponse.setStatus("failed");
            kafkaProducerService.sendPaymentResponse(key, correlationId, errorResponse);
        }
    }
}