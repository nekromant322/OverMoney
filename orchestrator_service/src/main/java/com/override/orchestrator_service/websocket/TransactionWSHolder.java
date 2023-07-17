package com.override.orchestrator_service.websocket;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TransactionWSHolder { //TODO Настройка мапы
    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionMapper transactionMapper;

    private final Map<Principal, TransactionDTO> transactionDTOMap = new ConcurrentHashMap<>();

    public void addTransactionDTO(Principal principal, TransactionDTO transactionDTO) {
        transactionDTOMap.putIfAbsent(principal, transactionDTO);
    }

    public TransactionDTO getTransactionDTOForPrincipalAndClear(Principal principal) {
        return transactionDTOMap.remove(principal);
    }

    public List<TransactionDTO> getAllTransactionDTOForPrincipal(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions = transactionService
                .findTransactionsListByUserIdWithoutCategories(telegramUtils.getTelegramId(principal));
        transactions.sort(Comparator.comparing(Transaction::getDate));

        return transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }







    private List<TransactionDTO> sendRecentlyAddTransactionToFront(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions = transactionService
                .findTransactionsListByUserIdWithoutCategories(telegramUtils.getTelegramId(principal));
        transactions.sort(Comparator.comparing(Transaction::getDate));

//        if (!transactions.isEmpty()) {
//            transactions.stream()
//                    .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
//                    .forEach(transactionDTO ->  transactionDTOMap.putIfAbsent(principal, transactionDTO));
//        }

        return transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }
}
