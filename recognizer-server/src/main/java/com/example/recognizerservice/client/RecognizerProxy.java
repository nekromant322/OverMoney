package com.example.recognizerservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="orchestrator-service")
public interface RecognizerProxy {
    @GetMapping("/orchestra")
    public String getOrchestra();
}
