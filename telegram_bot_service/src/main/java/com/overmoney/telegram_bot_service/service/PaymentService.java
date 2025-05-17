package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import com.override.dto.AccountDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
public class PaymentService {

    private final KafkaSubscriptionProducerService kafkaService;

    public String checkSubscription(AccountDataDTO dto) {
        try {
            return kafkaService.send(dto).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "Ошибка при получении подписки или истекло время ожидания.";
        }
    }
}

