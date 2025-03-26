package com.override.recognizer_service.llm;

import com.override.recognizer_service.config.LlamaOptionsProperties;
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
    private LlamaOptionsProperties options;
    private List<Message> messages;

    public LLMRequestDTO(String model, LlamaOptionsProperties options, List<Message> messages) {
        this.model = model;
        this.stream = false;
        this.options = options;
        this.messages = messages;
    }
}