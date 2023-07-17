package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.service.TransactionExelService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Slf4j
@RestController
public class TransactionFromExelController {
    @Autowired
    TransactionExelService transactionExelService;
    @Autowired
    TelegramUtils telegramUtils;

    @GetMapping("/loading")
    public ResponseEntity<String> loadingTransactionsFromExel(Principal principal, @RequestPart("data") MultipartFile file) {
        return transactionExelService.processTransactionExel(principal, file);
    }
}
