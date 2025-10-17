package com.override.payment_service.controller.rest;

import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaWebhookDTO;
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

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class YooKassaWebhookController {

    private final SubscriptionService subscriptionService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/yookassa")
    public ResponseEntity<String> handleYooKassaWebhook(@RequestBody YooKassaWebhookDTO notification) {
        log.info("Получен webhook от ЮКасса: {}", notification);

        try {
            if (notification == null || notification.getObject() == null) {
                log.error("Получен некорректный webhook: отсутствует объект платежа");
                return ResponseEntity.badRequest().body("Invalid webhook data");
            }

            YooKassaWebhookDTO.YooKassaPaymentObjectDTO paymentObject = notification.getObject();

            if (paymentObject.getId() == null || paymentObject.getId().isBlank()) {
                log.error("Получен webhook без ID платежа");
                return ResponseEntity.badRequest().body("Missing payment ID");
            }

            PaymentStatus paymentStatus;
            try {
                paymentStatus = convertYooKassaStatus(paymentObject.getStatus());
            } catch (IllegalArgumentException e) {
                log.error("Ошибка конвертации статуса платежа: {}", e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }

            subscriptionService.updateSubscriptionStatus(paymentObject.getId(), paymentStatus);

            if (paymentStatus == PaymentStatus.SUCCESS) {
                handleSuccessfulPayment(paymentObject.getId(), paymentStatus);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка обработки webhook от ЮКасса", e);
            return ResponseEntity.internalServerError().body("Webhook processing error");
        }
    }

    private void handleSuccessfulPayment(String paymentId, PaymentStatus status) {
        subscriptionService.getSubscriptionByPaymentId(paymentId).ifPresent(subscription -> {
            PaymentResponseDTO response = PaymentResponseDTO.builder()
                    .paymentId(paymentId)
                    .status(status)
                    .chatId(subscription.getChatId())
                    .build();

            kafkaProducerService.sendSubscriptionNotification(response);
        });
    }

    private PaymentStatus convertYooKassaStatus(String yooKassaStatus) {
        if (yooKassaStatus == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        String statusUpper = yooKassaStatus.toUpperCase();
        switch (statusUpper) {
            case "SUCCEEDED":
                return PaymentStatus.SUCCESS;
            case "PENDING":
                return PaymentStatus.PENDING;
            case "CANCELED":
                return PaymentStatus.CANCELED;
            default:
                log.error("Неизвестный статус платежа: {}", yooKassaStatus);
                throw new IllegalArgumentException("Unknown payment status: " + yooKassaStatus);
        }
    }
}