package com.override.orchestrator_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(value = "payment", url = "${integration.internal.host.payment}")
public interface PaymentFeign {

    @GetMapping("/payments/subscription/{chatId}/status")
    String getSubscriptionByChatId(@PathVariable("chatId") Long chatId);

    @GetMapping("/payments/pay/{chatId}")
    ResponseEntity<String> getPaymentUrl(@PathVariable Long chatId);

    @PostMapping(value = "/payments/result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> resultCallback(@SpringQueryMap Map<String, String> allParams);
}