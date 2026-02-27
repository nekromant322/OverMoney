package com.override.recognizer_service.llm.deepseek;

import com.override.recognizer_service.config.DeepSeekOptionsProperties;
import com.override.recognizer_service.llm.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepSeekRequestDTO {

    /**
     * используемая модель Deepseek.
     */
    private String model;

    /**
     * true - возвращает ответ потоком
     * false - возвращает полнгостью сформированный ответ
     */
    private boolean stream;

    /**
     * Параметр определяющий степень творчества модели от 0.0 до 2.0
     * чем ниже, тем точнее следует инструкциям
     */
    private float temperature;

    /**
     * определяяет ширину контекста которая модель рассматривает для ответа  от 0.0 до 1.0
     * чем выше, тем больше выборка
     */
    private float topP;

    private List<Message> messages;

    public DeepSeekRequestDTO(String model, DeepSeekOptionsProperties options, List<Message> messages) {
        this.model = model;
        this.stream = false;
        this.temperature = options.getTemperature();
        this.topP = options.getTopP();
        this.messages = messages;
    }
}
