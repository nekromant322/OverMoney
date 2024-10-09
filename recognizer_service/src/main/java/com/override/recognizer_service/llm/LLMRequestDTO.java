package com.override.recognizer_service.llm;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMRequestDTO {

    private String model;
    private List<Message> messages;
    private boolean stream;

    public LLMRequestDTO(String model, String message, List<String> categories) {
        this.model = model;
        this.stream = false;
        this.messages = List.of(
            new Message("system", "Ты помощник по определению категории транзакций. Категории: "
                + String.join(", ", categories)
                + " Ответ: название категории и уверенность (0-1). Пример: Продукты, 0.75 "
                + " Выбирай из представленных категорий"),
            new Message("user", String.format("Определи категорию для: \"%s\"", message))
        );
    }
}