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

    /**
     * Метод POST-запроса, переносящий только категории в новый аккаунт
     */
    @PostMapping("/merge/categories")
    public void mergeCategories(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndWithoutTransactions(userId);
    }

    /**
     * Метод POST-запроса, переносящий и категории, и транзакции в новый аккаунт
     */
    @PostMapping("/merge/transactions")
    public void mergeTransactions(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndTransactions(userId);
    }
}
