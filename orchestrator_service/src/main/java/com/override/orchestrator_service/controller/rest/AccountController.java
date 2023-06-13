package com.override.orchestrator_service.controller.rest;

import com.override.dto.AccountDataDTO;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private OverMoneyAccountService accountService;

    @PostMapping("/register")
    public void registerAccount(@RequestBody AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        accountService.registerOverMoneyAccount(accountDataDTO.getChatId(), accountDataDTO.getUserId());
    }

    @PostMapping("/mergeWithCategories")
    public void mergeAccountWithCategories(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategories(userId);
    }

    @PostMapping("/mergeWithCategoriesAndTransactions")
    public void mergeAccountWithCategoriesAndTransactions(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndTransactions(userId);
    }
}
