package com.override.recognizer_service.controller.rest;

import com.override.recognizer_service.service.DeepSeekBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recognizer/deepseek")
public class DeepSeekMetaInfoController {

    @Autowired
    private DeepSeekBalanceService deepSeekBalanceService;

    @GetMapping("/balance")
    public String getBalance() {
        return deepSeekBalanceService.getBalance();
    }
}
