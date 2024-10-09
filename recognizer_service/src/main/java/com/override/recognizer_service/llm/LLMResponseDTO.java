package com.override.recognizer_service.llm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMResponseDTO {

    private Message message;

    public String getCategoryName() {
        return message.getContent().split(",")[0].trim();
    }

    public float getAccuracy() {
        return Float.parseFloat(message.getContent().split(",")[1].trim());
    }
}