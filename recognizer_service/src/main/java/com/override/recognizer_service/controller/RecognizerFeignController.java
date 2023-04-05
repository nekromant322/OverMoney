package com.override.recognizer_service.controller;

import com.override.recognizer_service.client.RecognizerFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecognizerFeignController {

    @Autowired
    private RecognizerFeignClient feignClient;

    @GetMapping("/orchestra")
    public String getOrchestraWithFeign() {
        return feignClient.getOrchestra();
    }

    @GetMapping("/recognizer")
    public String getRecognizer() {
        return "Recognizer";
    }
}
