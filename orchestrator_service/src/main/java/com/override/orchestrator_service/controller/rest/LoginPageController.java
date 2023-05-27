package com.override.orchestrator_service.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginPageController {
    @Value("${telegram.bot.name}")
    private String botName;

    @GetMapping("/bot-login")
    public String getLogin(){
        return botName;
    }
}
