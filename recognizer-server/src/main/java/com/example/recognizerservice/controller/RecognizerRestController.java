package com.example.recognizerservice.controller;


import com.example.recognizerservice.client.RecognizerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecognizerRestController {
    @Autowired
    private RecognizerProxy proxy;

    @GetMapping("/recognizer")
    public String getRecognition() {
        return "I recognize you";
    }

    @GetMapping("/feign/orchestra")
    public String getOrchestraWithFeign() {
        return proxy.getOrchestra();
    }
}
