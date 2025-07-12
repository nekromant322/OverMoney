package com.override.payment_service.controller.rest;

import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.kafka.consumerproducer.KafkaProducerService;
import com.override.payment_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class YooKassaWebhookController {

    private final SubscriptionService subscriptionService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/yookassa")
    public ResponseEntity<String> handleYooKassaWebhook(@RequestBody Map<String, Object> notification) {
        log.info("Получен webhook от ЮКасса: {}", notification);

        try {
            Map<String, Object> object = (Map<String, Object>) notification.get("object");
            String paymentId = (String) object.get("id");
            String status = ((String) object.get("status")).toUpperCase();

            PaymentStatus paymentStatus;
            switch (status) {
                case "SUCCEEDED":
                    paymentStatus = PaymentStatus.SUCCESS;
                    break;
                case "PENDING":
                    paymentStatus = PaymentStatus.PENDING;
                    break;
                case "CANCELED":
                    paymentStatus = PaymentStatus.CANCELED;
                    break;
                default:
                    log.error("Неизвестный статус платежа: {}", status);
                    return ResponseEntity.badRequest().body("Unknown payment status");
            }

            subscriptionService.updateSubscriptionStatus(paymentId, paymentStatus);

            if (paymentStatus == PaymentStatus.SUCCESS) {
                subscriptionService.getSubscriptionByPaymentId(paymentId).ifPresent(subscription -> {
                    PaymentResponseDTO response = PaymentResponseDTO.builder()
                            .paymentId(paymentId)
                            .status(paymentStatus)
                            .chatId(subscription.getChatId())
                            .build();

                    kafkaProducerService.sendSubscriptionNotification(response);
                });
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка обработки webhook от ЮКасса", e);
            return ResponseEntity.badRequest().body("Invalid webhook data");
        }
    }
}