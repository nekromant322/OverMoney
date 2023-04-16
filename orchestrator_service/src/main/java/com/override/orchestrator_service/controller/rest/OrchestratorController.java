package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.feign.RecognizerFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrchestratorController {

    @Autowired
    private RecognizerFeign recognizerFeign;

    @GetMapping("/orchestra")
    public String getOrchestra() {
        log.info("GET request on /orchestra, test");
        return "Orchestra";
    }

    @GetMapping("/recognizer")
    public String getRecognizerWithFeign() {
        return recognizerFeign.getRecognition();
    }

}
