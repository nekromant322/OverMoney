package com.overmoney.telegram_bot_service.kafka.service;

import com.overmoney.telegram_bot_service.constants.KafkaConstants;
import com.override.dto.PaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaSubscriptionProducerService {

    private final KafkaTemplate<String, PaymentRequestDTO> kafkaTemplate;

    public String sendPaymentRequest(PaymentRequestDTO request) {
        Message<PaymentRequestDTO> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.PAYMENT_REQUESTS_TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, request.getOrderId())
                .setHeader(KafkaHeaders.CORRELATION_ID, UUID.randomUUID().toString())
                .build();

        kafkaTemplate.send(message)
                .addCallback(
                        result -> log.info("Успешно отправлен ответ на оплату подписки: {}", request.getOrderId()),
                        ex -> log.error("Не удалось отправить ответ на оплату подписки: {}", request.getOrderId(), ex)
                );

        return request.getOrderId();
    }
}