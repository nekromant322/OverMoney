package com.override.payment_service.controller.rest;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.service.YooKassaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final YooKassaService yooKassaService;

    @PostMapping
    public PaymentResponseDTO createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        return yooKassaService.createPayment(paymentRequest);
    }
}