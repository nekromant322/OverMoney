package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final YooKassaService yooKassaService;

    @Value("${subscription.duration.months}")
    private int subscriptionMonths;

    @Value("${subscription.payment.timeout-minutes}")
    private int paymentTimeoutMinutes;

    @Transactional
    public PaymentResponseDTO createOrGetExistingPayment(Long chatId, PaymentRequestDTO paymentRequest) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByChatId(chatId);

        if (existingSubscription.isPresent() &&
                existingSubscription.get().getPaymentUrlExpires().isAfter(LocalDateTime.now())) {

            return PaymentResponseDTO.builder()
                    .orderId(existingSubscription.get().getOrderId())
                    .paymentUrl(existingSubscription.get().getPaymentUrl())
                    .status(PaymentStatus.PENDING)
                    .build();
        }

        Subscription subscription = existingSubscription.orElseGet(() ->
                Subscription.builder()
                        .orderId(paymentRequest.getOrderId())
                        .build());

        subscription.setChatId(chatId);

        PaymentResponseDTO paymentResponse = yooKassaService.createPayment(paymentRequest);

        subscription.setPaymentUrl(paymentResponse.getPaymentUrl());
        subscription.setPaymentUrlExpires(LocalDateTime.now().plusMinutes(paymentTimeoutMinutes));
        subscription.setPaymentId(paymentResponse.getPaymentId());
        subscriptionRepository.save(subscription);
        log.info("Подписка сохранена, ID: {}", subscription.getId());

        return paymentResponse;
    }

    @Transactional
    public void updateSubscriptionStatus(String paymentId, PaymentStatus status) {
        if (paymentId == null || status == null) {
            log.warn("Попытка обновить подписку с null идентификатором платежа или статусом");
            return;
        }

        subscriptionRepository.findByPaymentId(paymentId).ifPresent(subscription -> {
            if (status == PaymentStatus.SUCCESS) {
                subscription.setStartDate(LocalDateTime.now());
                subscription.setEndDate(LocalDateTime.now().plusMonths(subscriptionMonths));
                subscription.setActive(true);
            }
            subscriptionRepository.save(subscription);
        });
    }

    public Optional<Subscription> getSubscriptionByChatId(Long chatId) {
        return subscriptionRepository.findByChatId(chatId);
    }

    public Optional<Subscription> getSubscriptionByPaymentId(String paymentId) {
        return subscriptionRepository.findByPaymentId(paymentId);
    }
}