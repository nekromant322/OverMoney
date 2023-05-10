package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.overmoney.telegram_bot_service.model.TransactionDTO;
import com.overmoney.telegram_bot_service.model.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorRequestService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    public TransactionResponseDTO sendTransaction(TransactionDTO transaction) {
       return orchestratorFeign.sendTransaction(transaction);
    }

    public void sendVoiceMessage(byte[] voiceMessage) {
        orchestratorFeign.sendVoiceMessage(voiceMessage);
    }
}
