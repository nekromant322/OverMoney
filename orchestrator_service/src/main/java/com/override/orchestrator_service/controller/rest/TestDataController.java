package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.util.TelegramUtils;
import com.override.orchestrator_service.util.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.management.InstanceNotFoundException;
import java.security.Principal;

@RestController
@Profile("dev")
public class TestDataController {
    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private TransactionUtils transactionUtils;

    @GetMapping("/testData")
    public RedirectView getTransactionsHistory(Principal principal,
                                               @RequestParam(defaultValue = "2500") Integer count)
            throws InstanceNotFoundException {
        try {
            transactionUtils.generateRandomTransactionsByPortion(telegramUtils.getTelegramId(principal), count);
            return new RedirectView("/analytics");
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Для генерации тестовых данных обязательно " +
                    "наличие распознанных транзакций." + "\n" + "\nНесколько доходных + несколько расходных");
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
    }
}
