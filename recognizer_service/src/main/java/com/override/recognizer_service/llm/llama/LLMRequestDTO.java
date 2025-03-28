package com.override.recognizer_service.llm.llama;

import com.override.recognizer_service.config.LlmOptionsProperties;
import com.override.recognizer_service.llm.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMRequestDTO {

    private String model;
    private boolean stream;
    /**
     * мета информации для настройки работы модели
     * <a href="https://api-docs.deepseek.com/api/create-chat-completion">deepseek API doc</a>
     */
    private LlmOptionsProperties options;
    private List<Message> messages;

    public LLMRequestDTO(String model, LlmOptionsProperties options, List<Message> messages) {
        this.model = model;
        this.stream = false;
        this.options = options;
        this.messages = messages;
    }
}