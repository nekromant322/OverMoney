package com.override.orchestrator_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/overview")
    public String getMainPage() {
        return "overview";
    }

    @GetMapping("/history")
    public String getTransactionPage() {
        return "history";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }
  
    @GetMapping("/analytics")
    public String getAnalytics() {
        return "analytics";
    }

    @GetMapping("/settings")
    public String getSettings() {
        return "settings";
    }

    @GetMapping("/admin/panel")
    public String getPanel() {
        return "admin_panel";
    }

    @GetMapping("/micromanagement")
    public String getMicromanagement() {
        return "micromanagement";
    }
}