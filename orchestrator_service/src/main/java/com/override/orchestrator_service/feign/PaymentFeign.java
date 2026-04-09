package com.override.orchestrator_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "payment", url = "${integration.internal.host.payment}")
public interface PaymentFeign {

    @GetMapping("/api/payment/test")
    String getTest();
}