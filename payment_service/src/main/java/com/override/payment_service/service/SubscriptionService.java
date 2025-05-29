package com.override.payment_service.service;

import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    //Todo
    public String getSubscriptionText(Long userId) {
        boolean hasSubscription = checkUserSubscription(userId);
        if (!hasSubscription) {
            return "У вас нет активной подписки";
        }
        return "Подписка активна до такого числа";
    }

    private boolean checkUserSubscription(Long userId) {
        return true;
    }
}
