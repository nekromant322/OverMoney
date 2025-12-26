package com.overmoney.telegram_bot_service.kafka.consumer;

import com.overmoney.telegram_bot_service.constants.KafkaConstants;
import com.overmoney.telegram_bot_service.service.TelegramNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaSubscriptionNotificationConsumer {

    private final TelegramNotificationService telegramNotificationService;

    public KafkaSubscriptionNotificationConsumer(TelegramNotificationService telegramNotificationService) {
        this.telegramNotificationService = telegramNotificationService;
    }

    @KafkaListener(
            topics = KafkaConstants.SUBSCRIPTION_NOTIFICATION_TOPIC,
            groupId = KafkaConstants.TELEGRAM_BOT_GROUP
    )
    public void listenForSubscriptionNotifications(ResponseEntity<String> response) {
        if (response.getBody().contains("OK")) {
            Long id = Long.parseLong(response.getBody().substring(2)); // "OK12" -> 12
            log.info("Получено уведомление об успешной оплате подписки для chatId: {}", id);
            telegramNotificationService.sendSubscriptionSuccessNotification(id);
        }
    }
}