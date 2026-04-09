package com.override.orchestrator_service.controller;

import com.override.orchestrator_service.feign.PaymentFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentTestController {

    private final PaymentFeign paymentFeign;

    @GetMapping("/test")
    public String test() {
        return paymentFeign.getTest();
    }
}
