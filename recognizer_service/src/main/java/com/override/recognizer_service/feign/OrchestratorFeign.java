package com.override.recognizer_service.feign;

import com.override.dto.TransactionDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "orchestrator-service")
public interface OrchestratorFeign {

    @GetMapping("/orchestra")
    String getOrchestra();

    @PutMapping("/transaction")
    ResponseEntity<String> setSuggestedCategory(@RequestBody TransactionDTO transactionDTO);
}
