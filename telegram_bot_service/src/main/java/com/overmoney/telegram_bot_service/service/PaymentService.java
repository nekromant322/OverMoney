package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.PaymentFeign;
import com.overmoney.telegram_bot_service.kafka.service.KafkaSubscriptionProducerService;
import com.override.dto.AccountDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final KafkaSubscriptionProducerService kafkaService;
    private final PaymentFeign paymentFeign;
    @Value("${service.transaction.processing}")
    private String switcher;

    public String checkSubscription(AccountDataDTO dto) {
        if (switcher.equals("kafka")) {
            try {
                return kafkaService.send(dto).get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                return "Ошибка при получении подписки или истекло время ожидания.";
            }
        } else {
            return paymentFeign.checkSubscription(dto);
        }
    }
}

