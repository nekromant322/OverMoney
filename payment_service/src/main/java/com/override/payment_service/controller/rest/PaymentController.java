package com.override.payment_service.controller.rest;

import com.override.payment_service.kafka.consumerproducer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final KafkaProducerService kafkaProducerService;

    @GetMapping("/status/{paymentId}")
    public String checkPaymentStatus(@PathVariable String paymentId) {
        // Implement status check with YooKassa API
        return "Payment status check endpoint";
    }
}