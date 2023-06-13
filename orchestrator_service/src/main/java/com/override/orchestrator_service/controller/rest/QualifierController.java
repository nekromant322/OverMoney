package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionQualifierDTO;
import com.override.orchestrator_service.service.KeywordService;
import com.override.orchestrator_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class QualifierController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private KeywordService keywordService;

    @PostMapping("/qualifier")
    public ResponseEntity<String> qualify(@RequestBody TransactionQualifierDTO transactionQualifierDTO) {
        try {
            transactionService.setCategoryForAllUndefinedTransactionsWithSameKeywords(transactionQualifierDTO.getTransactionId(),
                                                      transactionQualifierDTO.getCategoryId());

            keywordService.setKeywordCategory(transactionQualifierDTO.getTransactionId(),
                    transactionQualifierDTO.getCategoryId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}