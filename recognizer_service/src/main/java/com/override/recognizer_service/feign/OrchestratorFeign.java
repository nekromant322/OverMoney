package com.override.recognizer_service.feign;

import com.override.recognizer_service.config.FeignConfiguration;
import com.override.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "orchestrator-service", configuration = FeignConfiguration.class)
public interface OrchestratorFeign {

    @PutMapping("/transaction")
    ResponseEntity<String> editTransaction(@RequestBody TransactionDTO transactionDTO);
}
