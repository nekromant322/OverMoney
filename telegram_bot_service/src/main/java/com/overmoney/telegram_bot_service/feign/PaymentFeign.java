package com.overmoney.telegram_bot_service.feign;

import com.override.dto.AccountDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "payment", url = "${integration.internal.host.payment}")
public interface PaymentFeign {

    @PostMapping("/payment/check")
    String checkSubscription(AccountDataDTO accountDataDTO);
}
