package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TelegramUtils telegramUtils;

    @PostMapping("/transaction")
    public TransactionResponseDTO processTransaction(@RequestBody TransactionMessageDTO transactionMessage) throws InstanceNotFoundException {
        Transaction transaction = transactionProcessingService.processTransaction(transactionMessage);
        transactionService.saveTransaction(transaction);
        return transactionMapper.mapTransactionToTelegramResponse(transaction);
    }

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactionsList(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions =
                transactionService.findTransactionsListByUserIdWithoutCategories(telegramUtils.getTelegramId(principal));
        transactions.sort(Comparator.comparing(Transaction::getDate));

        return transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }

    @GetMapping("/transactions/history")
    public List<TransactionDTO> getTransactionsHistory(Principal principal,
                                                @RequestParam(defaultValue = "50") Integer pageSize,
                                                @RequestParam(defaultValue = "0") Integer pageNumber)
            throws InstanceNotFoundException {
        return transactionService
                .findTransactionsByUserIdLimited(telegramUtils.getTelegramId(principal), pageSize, pageNumber);
    }

    @GetMapping("/transactions/sum/{categoryId}")
    public Long getSumTransactionsByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return transactionService.getSumOfTransactionsByCategoryId(categoryId);
    }
}
