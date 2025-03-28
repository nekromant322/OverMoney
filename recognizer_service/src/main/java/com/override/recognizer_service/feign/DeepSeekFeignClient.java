package com.override.recognizer_service.feign;

import com.override.recognizer_service.llm.DeepSeekResponseWrapperDTO;
import com.override.recognizer_service.llm.deepseek.DeepSeekRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "DeepSeekRecognizer", url = "${deepseek.url}")
public interface DeepSeekFeignClient {

    @PostMapping(consumes = "application/json", produces = "application/json")
    DeepSeekResponseWrapperDTO recognizeCategory(@RequestHeader("Authorization") String authorizationHeader,
                                                 @RequestBody DeepSeekRequestDTO requestJson);
}
