package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionDefineDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.DefineService;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private DefineService defineService;

    @GetMapping("/transactions/count")
    public int getTransactionsCount() {
        return transactionService.getTransactionsCount();
    }

    @PostMapping("/transaction")
    public TransactionResponseDTO processTransaction(@RequestBody TransactionMessageDTO transactionMessage, Principal principal) throws InstanceNotFoundException {
        Transaction transaction = transactionProcessingService.validateAndProcessTransaction(transactionMessage, principal);
        transactionService.saveTransaction(transaction);
        transactionProcessingService.suggestCategoryToProcessedTransaction(transactionMessage, transaction.getId());
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

    @GetMapping("/transactions/info")
    public List<TransactionDTO> getTransactionsListByPeriodAndCategory(@RequestParam Integer year,
                                                                       @RequestParam Integer month,
                                                                       @RequestParam long categoryId) {
        return transactionService.getTransactionsListByPeriodAndCategory(year, month, categoryId);
    }

    @GetMapping("/transactions/history")
    public List<TransactionDTO> getTransactionsHistory(Principal principal,
                                                       @RequestParam(defaultValue = "50") Integer pageSize,
                                                       @RequestParam(defaultValue = "0") Integer pageNumber)
            throws InstanceNotFoundException {
        return transactionService
                .findTransactionsByUserIdLimited(telegramUtils.getTelegramId(principal), pageSize, pageNumber);
    }

    @PostMapping("/transaction/define")
    public ResponseEntity<String> define(@RequestBody TransactionDefineDTO transactionDefineDTO) {
        defineService.defineTransactionCategoryByTransactionIdAndCategoryId(transactionDefineDTO.getTransactionId(),
                transactionDefineDTO.getCategoryId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transaction/undefine")
    public ResponseEntity<String> undefine(@RequestBody TransactionDefineDTO transactionDefineDTO) {
        defineService.undefineTransactionCategoryAndKeywordCategory(transactionDefineDTO.getTransactionId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/transaction")
    public ResponseEntity<String> editTransaction(@RequestBody TransactionDTO transactionDTO) {
        transactionService.saveTransaction(transactionService.enrichTransactionWithSuggestedCategory(transactionDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/history/{id}")
    public TransactionDTO getTransactionById(@PathVariable("id") UUID id) {
        return transactionMapper.mapTransactionToDTO(transactionService.getTransactionById(id));
    }

    @PutMapping("/transactions")
    public void updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        transactionService.editTransaction(transactionDTO);
    }

    @DeleteMapping("/transaction/{id}")
    public void deleteTransactionById(@PathVariable("id") UUID id) {
        transactionService.deleteTransactionById(id);
    }

    @PatchMapping("/transaction/update/{id}")
    public TransactionResponseDTO patchTransaction(@RequestBody TransactionMessageDTO transactionMessage,
                                                   @PathVariable("id") UUID id) throws InstanceNotFoundException {
        return transactionService.patchTransaction(transactionMessage, id);
    }
}