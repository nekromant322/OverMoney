package com.override.recognizer_service.llm;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.config.LlamaOptionsProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMRequestDTO {

    private static final String SYSTEM_BASELINE_START = "Ты — ассистент по классификации транзакций. Твоя задача: " +
            "определить категорию финансовой транзакции на основе описания. Категории указаны ниже вместе с " +
            "примерами. Ответ должен содержать **ТОЛЬКО** название категории и уровень уверенности от 0.0 до 1.0 " +
            "(через запятую, с одним знакам после запятой). Если описание не подходит ни под одну категорию — " +
            "выбери наиболее близкую и укажи низкую уверенность (менее 0.7). Категории и примеры:\n\n";
    private static final String SYSTEM_BASELINE_ENDING = "Не добавляй пояснений. Не используй кавычки. " +
            "Только: <категория>, <уверенность>.";

    private String model;
    private boolean stream;
    private LlamaOptionsProperties options;
    private List<Message> messages;


    public LLMRequestDTO(String model, String message, List<CategoryDTO> categories, LlamaOptionsProperties options) {
        this.model = model;
        this.stream = false;
        this.options = options;
        this.messages = constructMessages(categories, message);
    }

    private List<Message> constructMessages(List<CategoryDTO> categories, String message) {
        List<Message> messages = new ArrayList<>();

        StringBuilder systemContentBuilder = new StringBuilder();
        systemContentBuilder.append(SYSTEM_BASELINE_START);
        for (CategoryDTO category : categories) {
            systemContentBuilder.append(category.getName()).append(":\n\n");
            List<KeywordIdDTO> keywordIdDTO = category.getKeywords();
            if (keywordIdDTO != null && !keywordIdDTO.isEmpty()) {
                keywordIdDTO.stream()
                        .sorted(Comparator.comparingInt(KeywordIdDTO::getFrequency))
                        .limit(3)
                        .map(KeywordIdDTO::getName)
                        .forEach(keyword -> systemContentBuilder.append(keyword).append("\n"));
            }
        }
        systemContentBuilder.append(SYSTEM_BASELINE_ENDING);

        messages.add(new Message("user", String.format("Определи категорию для: \"%s\"", message)));
        messages.add(new Message("system", systemContentBuilder.toString()));

        return messages;
    }
}