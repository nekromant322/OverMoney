package com.override.orchestrator_service.controller.rest;

import com.override.dto.AccountDataDTO;
import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.mapper.AccountMapper;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping("/overview")
@RestController
public class OverviewController {
    @Autowired
    private OverMoneyAccountService accountService;

    @GetMapping("/")
    public AccountDataDTO getAccountData(Principal principal) {
        return accountService
                .getAccountDataDTO(accountService.getAccountByUsername(((JwtAuthentication) principal).getUsername()));
    }
}
