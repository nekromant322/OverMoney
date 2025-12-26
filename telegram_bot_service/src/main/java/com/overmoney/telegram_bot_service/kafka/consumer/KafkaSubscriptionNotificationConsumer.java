package com.overmoney.telegram_bot_service.kafka.consumer;

import com.overmoney.telegram_bot_service.constants.KafkaConstants;
import com.overmoney.telegram_bot_service.service.TelegramNotificationService;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
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
    public void listenForSubscriptionNotifications(PaymentResponseDTO response) {
        if (response.getMessage().contains("OK")) {
            log.info("Получено уведомление об успешной оплате подписки для chatId: {}", response.getChatId());
            telegramNotificationService.sendSubscriptionSuccessNotification(response.getChatId());
        }
    }
}