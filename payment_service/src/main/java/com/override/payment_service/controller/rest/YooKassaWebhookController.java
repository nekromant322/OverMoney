package com.override.payment_service.controller.rest;

import com.override.dto.YooKassaWebhookDTO;
import com.override.payment_service.kafka.consumerproducer.KafkaProducerService;
import com.override.payment_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class YooKassaWebhookController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/yookassa")
    public ResponseEntity<String> handleYooKassaWebhook(@RequestBody YooKassaWebhookDTO notification) {
        log.info("Получен webhook от ЮКасса: {}", notification);
        return subscriptionService.handleYooKassaWebhook(notification);
    }
}