package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;

@RestController
public class TransactionController {

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @PostMapping("/transaction")
    public TransactionResponseDTO processTransaction(@RequestBody TransactionMessageDTO transactionMessage) throws InstanceNotFoundException {
            Transaction transaction = transactionProcessingService.processTransaction(transactionMessage);
            transactionService.saveTransaction(transaction);
            return transactionMapper.mapTransactionToTelegramResponse(transaction);
    }
}
