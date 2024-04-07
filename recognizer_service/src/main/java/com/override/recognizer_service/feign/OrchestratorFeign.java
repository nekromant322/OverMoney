package com.override.recognizer_service.feign;

import com.override.dto.TransactionDTO;
import com.override.recognizer_service.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "orchestrator", url = "${integration.internal.host.orchestrator}", configuration = FeignConfiguration.class)
public interface OrchestratorFeign {

    @PutMapping("/transaction")
    ResponseEntity<String> editTransaction(@RequestBody TransactionDTO transactionDTO);
}
