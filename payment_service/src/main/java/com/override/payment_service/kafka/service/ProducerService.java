package com.override.payment_service.kafka.service;

import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.kafka.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, PaymentResponseDTO> kafkaTemplate;

    public void sendSubscriptionNotification(PaymentResponseDTO response) {
        Message<PaymentResponseDTO> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.SUBSCRIPTION_NOTIFICATION_TOPIC)
                .build();

        kafkaTemplate.send(message)
                .addCallback(
                        result -> log.info(
                                "Уведомление о подписке отправлено для chatId: {}",
                                response.getChatId()),
                        ex -> log.error(
                                "Не удалось отправить уведомление о подписке",
                                ex)
                );
    }
}
