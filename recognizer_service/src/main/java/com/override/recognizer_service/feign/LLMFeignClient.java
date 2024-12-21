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

/*
Чтобы запустить в любом рандомном месте LLM, которая суммет отвечать на эти запросы надо
- docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
И установить туда модель (пример для llama3.1)
- docker exec -it ollama ollama run llama3.1
И подождать пару минут (или много минут если модель большая)
на порту 11434 должен появится текст "Ollama is running"
 */
