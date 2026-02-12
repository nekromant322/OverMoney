package com.override.payment_service.service;

import com.override.dto.SubscriptionDTO;
import com.override.payment_service.exceptions.RepeatPaymentException;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public Subscription findByChatId(Long chatId) {
        return subscriptionRepository.findByChatId(chatId).orElseThrow();
    }

    public SubscriptionDTO getSubscriptionWithStatusByChatId(Long chatId){
        try {
            Optional<Subscription> subscription = subscriptionRepository.findByChatId(chatId);
            if (subscription.orElseThrow().getEndDate().isBefore(LocalDateTime.now())) {
                subscriptionRepository.save(subscription.orElseThrow().setActive(false));
                return SubscriptionDTO.builder()
                        .isActive(subscription.orElseThrow().isActive())
                        .endDate(subscription.orElseThrow().getEndDate())
                        .build();
            } else {
                return SubscriptionDTO.builder()
                        .isActive(false)
                        .endDate(null)
                        .build();
            }
        } catch (RuntimeException e) {
            return SubscriptionDTO.builder()
                    .isActive(false)
                    .endDate(null)
                    .build();
        }
    }
    public Subscription getOrCreateSubscription(Long chatId) {
        Optional<Subscription> subscription = subscriptionRepository.findByChatId(chatId);
        if(subscription.isPresent()){
            if (subscription.orElseThrow().isActive()) {
                throw new RepeatPaymentException("Подписка уже активирована");
            }
            return subscription.orElseThrow();
        }
        return subscriptionRepository.save(new Subscription().setChatId(chatId));
    }
}
