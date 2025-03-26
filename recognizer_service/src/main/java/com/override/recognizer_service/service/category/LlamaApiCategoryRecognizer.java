package com.override.recognizer_service.service.category;

import com.override.dto.CategoryDTO;
import com.override.dto.constants.SuggestionAlgorithm;
import com.override.recognizer_service.config.LlmOptionsProperties;
import com.override.recognizer_service.feign.LlamaFeignClient;
import com.override.recognizer_service.llm.llama.LLMRequestDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import com.override.recognizer_service.llm.Message;
import com.override.recognizer_service.llm.MessageConstructor;
import com.override.recognizer_service.service.AbstractApiCategoryRecognizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "recognizer.llm-algo", havingValue = "ACTIVE")
public class LlamaApiCategoryRecognizer extends AbstractApiCategoryRecognizer {

    private final LlamaFeignClient llamaFeignClient;
    private final MessageConstructor messageConstructor;
    private final LlmOptionsProperties llmOptionsProperties;

    @Value("${llm.model}")
    private String model;

    @Override
    public LLMResponseDTO recognizeCategoryUsingAPI(String message, List<CategoryDTO> categories) {
        List<Message> messages = messageConstructor.construct(categories, message);
        LLMRequestDTO request = new LLMRequestDTO(model, llmOptionsProperties, messages);
        log.debug("Для распознования сообщения был зайдествован алгоритм: {}", SuggestionAlgorithm.DEEPSEEK);

        return llamaFeignClient.recognizeCategory(request);
    }

    @Override
    public SuggestionAlgorithm getAlgorithm() {
        return SuggestionAlgorithm.LLM;
    }
}
