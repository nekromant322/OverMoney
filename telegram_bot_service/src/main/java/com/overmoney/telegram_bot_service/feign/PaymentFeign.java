package com.overmoney.telegram_bot_service.feign;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.SubscriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "payment", url = "${integration.internal.host.payment}")
public interface PaymentFeign {

    @PostMapping("/payments")
    PaymentResponseDTO createPayment(@RequestBody PaymentRequestDTO paymentRequest);

    @PostMapping("/payments/subscription")
    PaymentResponseDTO createSubscriptionPayment(
            @RequestParam Long chatId,
            @RequestBody PaymentRequestDTO paymentRequest);

    @GetMapping("/payments/subscription/{chatId}/status")
    SubscriptionDTO getSubscriptionStatus(@PathVariable Long chatId);

    @GetMapping("/payments/{paymentId}/status")
    PaymentResponseDTO checkPaymentStatus(@PathVariable String paymentId);
}