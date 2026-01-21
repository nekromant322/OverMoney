package com.override.payment_service.repository;

import com.override.payment_service.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByChatId(Long chatId);

    List<Subscription> findByPayment_InvoiceId(Long paymentInvoiceId);
}