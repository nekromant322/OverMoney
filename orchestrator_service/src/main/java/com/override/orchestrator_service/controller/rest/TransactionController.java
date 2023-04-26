package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.model.TransactionMessageDTO;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;

@RestController
public class TransactionController {

    private final String TRANSACTION_PROCESSING_FAILED = "Мы не смогли распознать ваше сообщение. Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";

    @Autowired
    TransactionProcessingService transactionProcessingService;

    @Autowired
    UserService userService;

    @Autowired
    TransactionMapper transactionMapper;

    @PostMapping("/transaction")
    public String processTransaction(@RequestBody TransactionMessageDTO transactionMessage) {
        try {
            Transaction transaction = transactionProcessingService.processTransaction(transactionMessage);
            userService.addTransaction(userService.getUserByUsername(transactionMessage.getUsername()), transaction);
            return transactionMapper.mapTransactionToTelegramMessage(transaction);
        } catch (InstanceNotFoundException e) {
            return TRANSACTION_PROCESSING_FAILED;
        }
    }
}
