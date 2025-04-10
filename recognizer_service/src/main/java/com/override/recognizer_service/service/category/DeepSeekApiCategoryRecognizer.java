package com.override.recognizer_service.service.category;


import com.override.dto.CategoryDTO;
import com.override.dto.constants.SuggestionAlgorithm;
import com.override.recognizer_service.config.DeepSeekOptionsProperties;
import com.override.recognizer_service.feign.DeepSeekFeignClient;
import com.override.recognizer_service.llm.DeepSeekResponseWrapperDTO;
import com.override.recognizer_service.llm.LLMResponseDTO;
import com.override.recognizer_service.llm.Message;
import com.override.recognizer_service.llm.MessageConstructor;
import com.override.recognizer_service.llm.deepseek.DeepSeekRequestDTO;
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
@ConditionalOnProperty(name = "recognizer.deep-seek-algo", havingValue = "ACTIVE")
public class DeepSeekApiCategoryRecognizer extends AbstractApiCategoryRecognizer {

    private final DeepSeekFeignClient deepSeekFeignClient;
    private final MessageConstructor messageConstructor;
    private final DeepSeekOptionsProperties deepSeekOptionsProperties;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.auth-token}")
    private String authToken;

    @Override
    public LLMResponseDTO recognizeCategoryUsingAPI(String message, List<CategoryDTO> categories) {
        log.info("Для распознования сообщения был зайдествован алгоритм: {}", SuggestionAlgorithm.DEEPSEEK);
        List<Message> messages = messageConstructor.construct(categories, message, false);
        log.info("В LLM передан следующий контент: {}", messages);
        DeepSeekRequestDTO request = new DeepSeekRequestDTO(model, deepSeekOptionsProperties, messages);
        DeepSeekResponseWrapperDTO wrapper = deepSeekFeignClient.recognizeCategory("Bearer " + authToken, request);
        return new LLMResponseDTO(wrapper.getChoices().get(0).getMessage());
    }

    @Override
    public SuggestionAlgorithm getAlgorithm() {
        return SuggestionAlgorithm.DEEPSEEK;
    }
}