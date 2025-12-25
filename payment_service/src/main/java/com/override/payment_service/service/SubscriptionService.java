package com.override.payment_service.service;

import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public List<Subscription> findByChatId(Long chatId) {
        return subscriptionRepository.findByChatId(chatId);
    }

    public boolean checkActiveStatus(Subscription subscription) {
        if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
            subscriptionRepository.save(subscription.setActive(false));
            return false;
        } else {
            return true;
        }
    }
}
