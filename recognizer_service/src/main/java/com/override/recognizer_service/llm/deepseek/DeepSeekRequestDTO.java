package com.override.recognizer_service.llm.deepseek;

import com.override.recognizer_service.config.LlmOptionsProperties;
import com.override.recognizer_service.llm.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepSeekRequestDTO {

    private String model;
    private boolean stream;
    private float temperature;
    private float topP;
    private float repetitionPenalty;
    private List<Message> messages;

    public DeepSeekRequestDTO(String model, LlmOptionsProperties options, List<Message> messages) {
        this.model = model;
        this.stream = false;
        this.temperature = options.getTemperature();
        this.topP = options.getTopP();
        this.repetitionPenalty = options.getRepetitionPenalty();
        this.messages = messages;
    }
}
