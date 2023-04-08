package com.override.recognizer_service.controller.rest;

import com.override.recognizer_service.feign.OrchestratorFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecognizerController {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @GetMapping("/orchestra")
    public String getOrchestraWithFeign() {
        return orchestratorFeign.getOrchestra();
    }

    @GetMapping("/recognizer")
    public String getRecognizer() {
        return "Recognizer";
    }
}
