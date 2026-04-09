package com.override.orchestrator_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserViewController {

    @GetMapping("/")
    public RedirectView redirectToMainPage() {
        return new RedirectView("/overmoney");
    }

    @GetMapping("/overmoney")
    public String getMainPage() {
        return "overmoney";
    }

    @GetMapping("/offer")
    public String getOffer() {
        return "offer";
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

    @GetMapping("/micromanagement")
    public String getMicromanagement() {
        return "micromanagement";
    }
}