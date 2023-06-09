package com.override.orchestrator_service.controller.rest;

import com.override.dto.RegistrationDataDTO;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;

@RestController
public class RegistrationController {
    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    @PostMapping("/register")
    public void registerOverMoneyAccount(@RequestBody RegistrationDataDTO accountData) throws InstanceNotFoundException {
        overMoneyAccountService.saveOverMoneyAccount(accountData.getChatId(), accountData.getUserId());
    }
}
