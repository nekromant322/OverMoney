package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.feign.RecognizerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorController {

    @Autowired
    private RecognizerFeign recognizerFeign;

    @GetMapping("/orchestra")
    public String getOrchestra() {
        return "Orchestra";
    }

    @GetMapping("/recognizer")
    public String getRecognizerWithFeign() {
        return recognizerFeign.getRecognition();
    }

}
