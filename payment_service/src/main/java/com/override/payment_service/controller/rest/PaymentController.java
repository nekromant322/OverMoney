package com.override.payment_service.controller.rest;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.SubscriptionDTO;
import com.override.payment_service.service.SubscriptionService;
import com.override.payment_service.service.YooKassaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final YooKassaService yooKassaService;
    private final SubscriptionService subscriptionService;

    @PostMapping
    public PaymentResponseDTO createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        return yooKassaService.createPayment(paymentRequest);
    }

    @GetMapping("/{paymentId}/status")
    public PaymentResponseDTO checkPaymentStatus(@PathVariable String paymentId) {
        PaymentResponseDTO response = yooKassaService.checkPaymentStatus(paymentId);
        subscriptionService.updateSubscriptionStatus(paymentId, response.getStatus());
        return response;
    }

    @PostMapping("/subscription")
    public PaymentResponseDTO createSubscriptionPayment(
            @RequestParam Long chatId,
            @RequestBody PaymentRequestDTO paymentRequest) {
        return subscriptionService.createOrGetExistingPayment(chatId, paymentRequest);
    }

    @GetMapping("/subscription/{chatId}/status")
    public SubscriptionDTO getSubscriptionStatus(@PathVariable Long chatId) {
        return subscriptionService.getSubscriptionByChatId(chatId)
                .map(sub -> SubscriptionDTO.builder()
                        .chatId(sub.getChatId())
                        .endDate(sub.getEndDate())
                        .isActive(sub.isActive())
                        .build())
                .orElse(SubscriptionDTO.builder()
                        .chatId(chatId)
                        .isActive(false)
                        .build());
    }
}