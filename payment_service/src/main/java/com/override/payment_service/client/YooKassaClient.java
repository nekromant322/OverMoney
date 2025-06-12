package com.override.payment_service.client;

import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.payment_service.config.YooKassaFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "yookassa-client", url = "https://api.yookassa.ru/v3", configuration = YooKassaFeignConfig.class)
public interface YooKassaClient {
    @PostMapping("/payments")
    YooKassaResponseDTO createPayment(
            @RequestHeader("Idempotence-Key") String idempotenceKey,
            YooKassaRequestDTO request);
}