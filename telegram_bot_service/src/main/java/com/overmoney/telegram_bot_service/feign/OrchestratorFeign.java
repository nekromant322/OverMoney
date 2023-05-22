package com.overmoney.telegram_bot_service.feign;

import com.override.dto.RegistrationDataDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="orchestrator-service")
public interface OrchestratorFeign {

    @PostMapping("/transaction")
    TransactionResponseDTO sendTransaction(@RequestBody TransactionMessageDTO transaction);

    @PostMapping("/voice_message")
    void sendVoiceMessage(@RequestBody byte[] voiceMessage);

    @PostMapping("/register")
    void registerOverMoneyAccount(@RequestBody RegistrationDataDTO accountData);
}
