package com.override.orchestrator_service.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrchestratorController {

    @GetMapping("/orchestra")
    public String getOrchestra() {
        log.info("GET request on /orchestra, test");
        return "Orchestra";
    }
}