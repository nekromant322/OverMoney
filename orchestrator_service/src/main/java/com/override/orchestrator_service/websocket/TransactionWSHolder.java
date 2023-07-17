package com.override.orchestrator_service.websocket;

import com.override.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransactionWSHolder {

    private final Map<Principal, TransactionDTO> transactionDTOMap = new ConcurrentHashMap<>();

    public void addTransactionDTO(Principal principal, TransactionDTO transactionDTO) {
        transactionDTOMap.putIfAbsent(principal, transactionDTO);
    }

    public TransactionDTO getTransactionDTOForPrincipalAndClear(Principal principal) {
        return transactionDTOMap.remove(principal);
    }
}
