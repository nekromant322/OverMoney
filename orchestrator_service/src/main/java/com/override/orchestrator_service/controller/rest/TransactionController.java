package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.override.orchestrator_service.util.TelegramUtils.getTelegramId;

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

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactionsList(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions = transactionService.findTransactionsListByUserId(getTelegramId(principal));
        transactions.removeIf(transaction -> transaction.getCategory() != null);
        return transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }
}
