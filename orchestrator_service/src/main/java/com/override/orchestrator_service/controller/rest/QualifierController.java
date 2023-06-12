package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionQualifierDTO;
import com.override.orchestrator_service.service.KeywordService;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class QualifierController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private KeywordService keywordService;
    @Autowired
    private TelegramUtils telegramUtils;

    @PostMapping("/qualifier")
    public ResponseEntity<String> qualify(@RequestBody TransactionQualifierDTO transactionQualifierDTO,
                                          Principal principal) {
        try {
            transactionService.setTransactionCategory(transactionQualifierDTO.getTransactionId(),
                    transactionQualifierDTO.getCategoryId(), telegramUtils.getTelegramId(principal));

            keywordService.setKeywordCategory(transactionQualifierDTO.getTransactionId(),
                    transactionQualifierDTO.getCategoryId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}