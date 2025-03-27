package com.override.recognizer_service.feign;


import com.override.recognizer_service.config.DeepSeekFeignConfig;
import com.override.recognizer_service.llm.DeepSeekResponseWrapperDTO;
import com.override.recognizer_service.llm.deepseek.DeepSeekBalanceResponseDTO;
import com.override.recognizer_service.llm.deepseek.DeepSeekRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "DeepSeekRecognizer", url = "${deepseek.base-url}", configuration = DeepSeekFeignConfig.class)
public interface DeepSeekFeignClient {

    @PostMapping(value = "/v1/chat/completions", consumes = "application/json", produces = "application/json")
    DeepSeekResponseWrapperDTO recognizeCategory(@RequestBody DeepSeekRequestDTO requestJson);

    @GetMapping(path = "/user/balance", consumes = "application/json", produces = "application/json")
    DeepSeekBalanceResponseDTO getBalance();
}
