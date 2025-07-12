package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, PaymentResponseDTO> kafkaTemplate;

    public void sendPaymentResponse(String key, String correlationId, PaymentResponseDTO response) {
        Message<PaymentResponseDTO> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.PAYMENT_RESPONSES_TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, key)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .build();

        kafkaTemplate.send(message)
                .addCallback(
                        result -> log.info("Успешно отправлен ответ на оплату заказа: {}", response.getOrderId()),
                        ex -> log.error("Не удалось отправить ответ на оплату заказа: {}", response.getOrderId(), ex)
                );
    }

    public void sendSubscriptionNotification(PaymentResponseDTO response) {
        Message<PaymentResponseDTO> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.SUBSCRIPTION_NOTIFICATION_TOPIC)
                .build();

        kafkaTemplate.send(message)
                .addCallback(
                        result -> log.info("Уведомление о подписке отправлено для paymentId: {}", response.getPaymentId()),
                        ex -> log.error("Не удалось отправить уведомление о подписке", ex)
                );
    }
}