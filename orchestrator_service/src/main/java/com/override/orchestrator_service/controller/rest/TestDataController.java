package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.util.TelegramUtils;
import com.override.orchestrator_service.util.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.management.InstanceNotFoundException;
import java.security.Principal;

//TODO убрать перед релизом
@RestController
public class TestDataController {
    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private TransactionUtils transactionUtils;

    @GetMapping("/testData")
    public RedirectView getTransactionsHistory(Principal principal,
                                               @RequestParam(defaultValue = "2500") Integer count) throws InstanceNotFoundException {
        transactionUtils.generateRandomTransactionsByPortion(telegramUtils.getTelegramId(principal), count);
        return new RedirectView("/analytics");
    }
}
