package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.OnboardingMessage;
import com.overmoney.telegram_bot_service.repository.OnboardingMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class OnboardingMessageService {
    @Autowired
    private OnboardingMessageRepository onboardingMessageRepository;

    public List<OnboardingMessage> getAllOnboardingMessages() {
        return onboardingMessageRepository.findAll();
    }

    public Set<UUID> findAllIds() {
        return onboardingMessageRepository.findAllIds();
    }

    public void deleteAllById(Set<UUID> toDelete) {
        onboardingMessageRepository.deleteAllById(toDelete);
    }

    public Optional<OnboardingMessage> findById(UUID messageId) {
        return onboardingMessageRepository.findById(messageId);
    }

    public void saveAll(List<OnboardingMessage> toSave) {
        onboardingMessageRepository.saveAll(toSave);
    }
}