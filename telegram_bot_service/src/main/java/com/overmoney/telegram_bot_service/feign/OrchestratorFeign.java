package com.overmoney.telegram_bot_service.feign;

import com.overmoney.telegram_bot_service.model.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="orchestrator-service")
public interface OrchestratorFeign {

    @PostMapping("/transaction")
    String sendTransaction(@RequestBody TransactionDTO transaction);
}
