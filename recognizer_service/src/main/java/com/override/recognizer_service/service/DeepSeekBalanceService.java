package com.override.recognizer_service.service;


import com.override.recognizer_service.llm.deepseek.DeepSeekBalanceResponseDTO;
import com.override.recognizer_service.feign.DeepSeekFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DeepSeekBalanceService {

    @Autowired
    private DeepSeekFeignClient client;

    @Value("${deepseek.auth-token}")
    private String authToken;

    public String getBalance() {
        DeepSeekBalanceResponseDTO dto = client.getBalance("Bearer " + authToken);
        return dto.getBalanceInfos().get(0).getTotalBalance();
    }
}
