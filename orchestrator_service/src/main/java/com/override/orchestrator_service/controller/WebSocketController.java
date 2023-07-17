package com.override.orchestrator_service.controller;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class WebSocketController {
    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionMapper transactionMapper;

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public List<TransactionDTO> getPrivateMessage(String message, Principal principal) throws InstanceNotFoundException {
        sendRecentlyAddTransactionToFront(principal)
                .forEach(tr -> log.info("-> Что мы отправляем на фронт: " + tr.toString()));
        return sendRecentlyAddTransactionToFront(principal);
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
