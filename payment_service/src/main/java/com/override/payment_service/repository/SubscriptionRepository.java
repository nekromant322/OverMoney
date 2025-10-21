package com.override.payment_service.repository;

import com.override.payment_service.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByChatId(Long chatId);

    Optional<Subscription> findByOrderId(String orderId);

    Optional<Subscription> findByPaymentId(String paymentId);
}