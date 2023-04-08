package com.override.recognizer_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="orchestrator-service")
public interface OrchestratorFeign {

    @GetMapping("/orchestra")
    public String getOrchestra();
}
