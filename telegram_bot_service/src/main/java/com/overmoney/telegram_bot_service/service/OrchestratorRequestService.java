package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.RegistrationDataDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorRequestService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    public TransactionResponseDTO sendTransaction(TransactionMessageDTO transaction) {
       return orchestratorFeign.sendTransaction(transaction);
    }

    public void registerOverMoneyAccount(RegistrationDataDTO accountData) {
        orchestratorFeign.registerOverMoneyAccount(accountData);
    }
}
