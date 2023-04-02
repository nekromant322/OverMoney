package com.example.orchestratorservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="recognizer-service")
public interface OrchestratorProxy {

    @GetMapping("/recognizer")
    public String getRecognition();
}
