package com.override.orchestrator_service.controller;

import com.override.orchestrator_service.client.OrchestratorFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorRestController {
    @Autowired
    OrchestratorFeignClient feignClient;

    @GetMapping("/orchestra")
    public String getOrchestra() {
        return "Orchestra";
    }

    @GetMapping("/recognizer")
    public String getRecognizerWithFeign() {
        return feignClient.getRecognition();
    }

}
