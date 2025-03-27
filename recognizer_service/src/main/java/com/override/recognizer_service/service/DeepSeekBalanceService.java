package com.override.recognizer_service.service;


import com.override.recognizer_service.llm.deepseek.DeepSeekBalanceResponseDTO;
import com.override.recognizer_service.feign.DeepSeekFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeepSeekBalanceService {

    @Autowired
    DeepSeekFeignClient client;

    public String getBalance() {
        DeepSeekBalanceResponseDTO dto = client.getBalance();
        return dto.getBalanceInfos().get(0).getTotalBalance();
    }
}
