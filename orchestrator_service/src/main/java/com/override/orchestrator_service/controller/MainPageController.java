package com.override.orchestrator_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/overview")
    public String getMainPage() {
        return "overview";
    }

    @GetMapping("/analytics")
    public String getAnalytics() {
        return "analytics";
    }
}
