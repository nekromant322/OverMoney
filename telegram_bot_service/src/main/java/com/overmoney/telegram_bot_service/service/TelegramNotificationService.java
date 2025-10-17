package com.overmoney.telegram_bot_service.service;

import com.override.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final PaymentService paymentService;
    private final AbsSender absSender;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void sendSubscriptionSuccessNotification(Long chatId) {
        SubscriptionDTO subscription = paymentService.getSubscriptionStatus(chatId);

        if (subscription.isActive()) {
            String messageText = String.format(
                    "Ваша подписка успешно активирована и будет действовать до %s",
                    subscription.getEndDate().format(formatter)
            );

            SendMessage message = new SendMessage(chatId.toString(), messageText);
            try {
                absSender.execute(message);
                log.info("Уведомление об успешной подписке отправлено для chatId: {}", chatId);
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке уведомления об успешной подписке", e);
            }
        }
    }
}