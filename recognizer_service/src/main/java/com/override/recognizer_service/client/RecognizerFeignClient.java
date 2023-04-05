package com.override.recognizer_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="orchestrator-service")
public interface RecognizerFeignClient {

    @GetMapping("/orchestra")
    public String getOrchestra();
}
