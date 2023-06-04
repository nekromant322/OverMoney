package com.override.orchestrator_service.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransactionPageController {

    @GetMapping("/transaction")
    public String getTransactionPage() {
        return "transaction";
    }
}
