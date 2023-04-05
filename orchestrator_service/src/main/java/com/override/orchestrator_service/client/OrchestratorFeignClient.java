package com.override.orchestrator_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="recognizer-service")
public interface OrchestratorFeignClient {

    @GetMapping("/recognizer")
    public String getRecognition();

}
