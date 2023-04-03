package com.override.orchestrator_service.controller;

import com.override.orchestrator_service.client.OrchestratorFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorRestController {
    @Autowired
    private OrchestratorFeign feign;

    @GetMapping("/recognizer")
    public String getRecognition() {
        return feign.getRecognition();
    }

    @GetMapping("/orchestra")
    public String getOrchestra() {
        return "Sax, Trombone, Keys, Drums";
    }

}
