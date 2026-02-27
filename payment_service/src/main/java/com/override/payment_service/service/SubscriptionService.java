package com.override.payment_service.service;

import com.override.dto.SubscriptionStatusDTO;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public SubscriptionStatusDTO getSubscriptionStatus(Long chatId) {
        boolean isActive = subscriptionRepository.findByChatId(chatId).map(Subscription::isActive).orElse(false);
        return SubscriptionStatusDTO.builder()
                .isActive(isActive)
                .build();
    }

    @Transactional
    public Subscription getOrCreateSubscription(Long chatId) {
        Optional<Subscription> subscription = subscriptionRepository.findByChatId(chatId);
        return subscription.orElseGet(() -> subscriptionRepository.save(new Subscription().setChatId(chatId)));
    }

    @Transactional
    public Subscription attachPayment(Subscription subscription, Payment payment) {
        Optional.ofNullable(subscription.getPayment())
                .ifPresent(oldPayment -> subscription.setPayment(null));
        subscription.setPayment(payment);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription activateSubscription(Payment payment) {
        Subscription subscription = subscriptionRepository
                .findByPayment(payment)
                .orElseThrow(() -> new IllegalStateException(
                        "Subscription not found for invoice: " + payment.getInvoiceId()
                ));

        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setActive(true);
        return subscriptionRepository.save(subscription);
    }
}
