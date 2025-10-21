package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.OnboardingMessage;
import com.overmoney.telegram_bot_service.model.User;
import com.overmoney.telegram_bot_service.model.UserOnboardingMessage;
import com.overmoney.telegram_bot_service.repository.UserOnboardingMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class UserOnboardingMessageService {

    @Autowired
    private UserOnboardingMessageRepository userOnboardingMessageRepository;

    public void saveUserOnboardingMessage(User user,
                                          OnboardingMessage onboardingMessage,
                                          LocalDateTime currentDate) {
        userOnboardingMessageRepository.save(UserOnboardingMessage.builder()
                .user(user)
                .onboardingMessage(onboardingMessage)
                .sentAt(currentDate)
                .build());
    }

    public boolean existsByUserIdAndOnboardingMessageId(Long userId, UUID onboardingMessageId) {
        return userOnboardingMessageRepository.existsByUserIdAndOnboardingMessageId(userId, onboardingMessageId);
    }

    public void deleteAllByOnboardingMessageIdIn(Set<UUID> toDelete) {
        userOnboardingMessageRepository.deleteAllByOnboardingMessageIdIn(toDelete);
    }
}

