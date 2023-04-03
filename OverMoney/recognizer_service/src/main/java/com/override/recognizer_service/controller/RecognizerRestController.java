package com.override.recognizer_service.controller;

import com.override.recognizer_service.client.RecognizerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecognizerRestController {
    @Autowired
    private RecognizerFeign feign;

    @GetMapping("/recognizer")
    public String getRecognition() {
        return "I recognize you";
    }

    @GetMapping("/orchestra")
    public String getOrchestraWithFeign() {
        return feign.getOrchestra();
    }
}
