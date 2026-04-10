package com.override.payment_service.schedler;

import com.override.payment_service.model.Subscription;
import com.override.payment_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionSchedulerService {
    private final SubscriptionService subscriptionService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateSubscriptionTimer() {
        System.out.println(LocalDateTime.now().plusHours(3));
        List<Subscription> listOfEndsSubscriptions = subscriptionService.findAllByEndDateBeforeAndActiveTrue(
                (LocalDateTime.now().plusHours(3)));
        listOfEndsSubscriptions.forEach(subscription -> subscription.setActive(false));
        subscriptionService.saveAll(listOfEndsSubscriptions);
    }
}
