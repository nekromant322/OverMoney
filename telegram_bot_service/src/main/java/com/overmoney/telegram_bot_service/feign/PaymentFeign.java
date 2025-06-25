package com.overmoney.telegram_bot_service.feign;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "payment", url = "${integration.internal.host.payment}")
public interface PaymentFeign {

    @PostMapping("/payments")
    PaymentResponseDTO createPayment(@RequestBody PaymentRequestDTO paymentRequest);
}