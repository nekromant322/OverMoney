package com.override.payment_service.service;

import com.override.dto.SubscriptionStatusDTO;
import com.override.payment_service.model.Payment;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public SubscriptionStatusDTO getAndUpdateSubscriptionStatus(Long chatId) {
        Optional<Subscription> subscription = subscriptionRepository.findByChatId(chatId);
        if (subscription.isPresent()) {
            boolean isActive = subscription.get().getEndDate().isAfter(ZonedDateTime.now(ZoneId.of("Europe/Moscow")));
            return SubscriptionStatusDTO.builder().isActive(isActive).build();
        }
        return SubscriptionStatusDTO.builder().isActive(false).build();
    }

    @Transactional
    public Subscription getOrCreateSubscription(Long chatId) {
        Optional<Subscription> subscription = subscriptionRepository.findByChatId(chatId);
        return subscription.orElseGet(() -> subscriptionRepository.save(new Subscription().setChatId(chatId)));
    }

    @Transactional
    public Subscription attachPayment(Subscription subscription, Payment payment) {
        Optional.ofNullable(subscription.getPayment()).ifPresent(oldPayment -> subscription.setPayment(null));
        subscription.setPayment(payment);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription activateSubscription(Payment payment) {
        Subscription subscription = subscriptionRepository.findByPayment(payment)
                .orElseThrow(() -> new IllegalStateException("Subscription not found for invoice: "
                        + payment.getInvoiceId()));

        subscription.setStartDate(ZonedDateTime.now(ZoneId.of("Europe/Moscow")));
        subscription.setEndDate(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusMonths(1));
        return subscriptionRepository.save(subscription);
    }
}
