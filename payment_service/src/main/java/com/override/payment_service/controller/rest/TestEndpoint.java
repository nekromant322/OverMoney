package com.override.payment_service.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class TestEndpoint {

    @GetMapping("/test")
    public String test() {
        return "Test endpoint";
    }
}
