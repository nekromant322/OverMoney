package com.override.recognizer_service.feign;

import com.override.recognizer_service.llm.LLMRequestDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "llmRecognizer", url = "${llm.url}")
public interface LLMFeignClient {

    @PostMapping(consumes = "application/json", produces = "application/json")
    LLMResponseDTO recognizeCategory(@RequestBody LLMRequestDTO requestJson);
}
