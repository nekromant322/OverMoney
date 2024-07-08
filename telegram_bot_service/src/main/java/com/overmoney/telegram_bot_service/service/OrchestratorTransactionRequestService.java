package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "service", havingValue = "orchestrator")
public class OrchestratorTransactionRequestService implements RequestService{

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @Override
    public TransactionResponseDTO sendTransaction(TransactionMessageDTO transaction) {
        return orchestratorFeign.sendTransaction(transaction);
    }
}
