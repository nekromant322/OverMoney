package com.override.recognizer_service.feign;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "orchestrator-service")
public interface OrchestratorFeign {

    @GetMapping("/orchestra")
    String getOrchestra();

    @PutMapping("/suggested-category")
    ResponseEntity<String> editTransaction(@RequestBody TransactionDTO transactionDTO);
}
