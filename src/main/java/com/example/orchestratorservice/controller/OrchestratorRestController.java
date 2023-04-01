package com.example.orchestratorservice.controller;

import com.example.orchestratorservice.client.OrchestratorProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorRestController {
    @Autowired
    OrchestratorProxy proxy;

    @GetMapping("/orchestra")
    public String getOrchestra() {
        return "Sax, Bass, Trombone, Piano";
    }

    @GetMapping("/feign/recognizer")
    public String getRecognitionWithFeign() {
        return proxy.getRecognition();
    }
}
