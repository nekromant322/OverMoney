package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.AccountDataDTO;
import com.override.payment_service.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaSubscriptionRequestConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SubscriptionService subscriptionService;

    @Value("${spring.kafka.topics.subscription-response}")
    private String subscriptionResponseTopic;

    public KafkaSubscriptionRequestConsumer(KafkaTemplate<String, String> kafkaTemplate,
                                            SubscriptionService subscriptionService) {
        this.kafkaTemplate = kafkaTemplate;
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.subscription-request}", groupId = "subscription-consumer")
    public void receiveSubscriptionRequest(AccountDataDTO dto) {
        log.info("Получен запрос подписки для userId: {}", dto.getUserId());

        String subscriptionInfo = subscriptionService.getSubscriptionText(dto.getUserId());

        // Отправка ответа с userId как key
        kafkaTemplate.send(subscriptionResponseTopic, dto.getUserId().toString(), subscriptionInfo);
    }
}
