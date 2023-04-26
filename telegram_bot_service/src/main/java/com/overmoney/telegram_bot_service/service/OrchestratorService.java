package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.overmoney.telegram_bot_service.model.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    public String sendTransaction(TransactionDTO transaction) {
       return orchestratorFeign.sendTransaction(transaction);
    }
}
