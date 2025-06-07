package com.overmoney.telegram_bot_service.kafka.consumer;

import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@Slf4j
public class KafkaSubscriptionConsumer {

    @Autowired
    private KafkaSubscriptionProducerService producerService;

    @KafkaListener(topics = "${spring.kafka.topics.subscription-response}")
    public void receiveSubscriptionResponse(
            String message,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key
    ) {
        log.info("Received subscription response for user {}: {}", key, message);
        producerService.complete(Long.parseLong(key), message);
    }
}
