package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.OverMoneyBot;
import com.overmoney.telegram_bot_service.model.OnboardingMessage;
import com.overmoney.telegram_bot_service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OnboardingMessageDispatchService {
    @Autowired
    private OnboardingMessageService onboardingMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOnboardingMessageService userOnboardingMessageService;

    @Autowired
    private OverMoneyBot overMoneyBot;

    public void checkAndDispatch() {

        List<OnboardingMessage> onboardingMessages = onboardingMessageService.getAllOnboardingMessages();
        if (!isValidMessages(onboardingMessages)) {
            return;
        }

        int maxDayDelay = onboardingMessages.stream()
                .mapToInt(OnboardingMessage::getDayDelay)
                .max()
                .orElse(-1);

        if (maxDayDelay < 0) {
            log.warn("No valid day delay found");
            return;
        }

        LocalDateTime currentDate = LocalDateTime.now();
        List<User> users = userService.getUsersRegistrationDateBefore(currentDate.minusDays(maxDayDelay));

        if (!isValidUsers(users)) {
            return;
        }

        for (OnboardingMessage onboardingMessage : onboardingMessages) {
            for (User user : users) {
                if (shouldSendMessage(user, onboardingMessage, currentDate)) {
                    userOnboardingMessageService.saveUserOnboardingMessage(user, onboardingMessage, currentDate);
                    overMoneyBot.sendPhotoWithCaption(user.getId(),
                            onboardingMessage.getImage(),
                            onboardingMessage.getMessage());
                }
            }
        }
    }

    private boolean isValidUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            log.warn("Список пользователей пуст");
            return false;
        }

        return true;
    }

    private boolean isValidMessages(List<OnboardingMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            log.warn("Список сообщений пуст");
            return false;
        }
        return true;
    }

    private boolean shouldSendMessage(User user, OnboardingMessage message, LocalDateTime currentDate) {
        boolean isAlreadySent = userOnboardingMessageService.
                existsByUserIdAndOnboardingMessageId(user.getId(),
                        message.getId());
        if (isAlreadySent) {
            return false;
        }
        LocalDateTime registrationDate = user.getRegistrationDate();
        LocalDateTime scheduledDate = registrationDate.plusDays(message.getDayDelay());

        return currentDate.isAfter(scheduledDate);
    }
}