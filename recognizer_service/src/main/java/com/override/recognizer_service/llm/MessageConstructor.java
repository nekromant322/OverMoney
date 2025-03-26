package com.override.recognizer_service.llm;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageConstructor {

    @Value("${llm.limit.keywords}")
    private int keyWordsLimit;

    @Value("${llm.limit.verbose-categories}")
    private int verboseCategoriesLimit;

    private static final String SYSTEM_BASELINE_START = "Ты — ассистент по классификации транзакций. Твоя задача: " +
            "определить категорию финансовой транзакции на основе описания. Категории указаны ниже вместе с " +
            "примерами. Ответ должен содержать **ТОЛЬКО** название категории и уровень уверенности от 0.0 до 1.0 " +
            "(через запятую, с одним знакам после запятой). Если описание не подходит ни под одну категорию — " +
            "выбери наиболее близкую и укажи низкую уверенность (менее 0.7). Категории и примеры:\n\n";

    private static final String SYSTEM_BASELINE_ENDING = "Не добавляй пояснений. Не используй кавычки. " +
            "Только: <категория>, <уверенность>.";

    public List<Message> construct(List<CategoryDTO> categories, String message) {
        List<Message> messages = new ArrayList<>();

        messages.add(constructSystemMessage(categories));
        messages.add(new Message("user", String.format("Определи категорию для: \"%s\"", message)));
        return messages;
    }

    private Message constructSystemMessage(List<CategoryDTO> categories) {

        StringBuilder systemContentBuilder = new StringBuilder();
        systemContentBuilder.append(SYSTEM_BASELINE_START);
        categories.sort(Comparator.comparing(
                categoryDTO -> {
                    int overallUsage = categoryDTO
                            .getKeywords()
                            .stream()
                            .map(KeywordIdDTO::getFrequency)
                            .reduce(0, Integer::sum);
                    return -overallUsage;
                }));

        for (CategoryDTO category : categories.stream().limit(verboseCategoriesLimit).collect(Collectors.toList())) {

            systemContentBuilder.append(":\n").append(category.getName()).append(":\n");
            List<KeywordIdDTO> keywordIdDTO = category.getKeywords();
            if (keywordIdDTO != null && !keywordIdDTO.isEmpty()) {
                keywordIdDTO.stream()
                        .sorted(Comparator.comparingInt(KeywordIdDTO::getFrequency).reversed())
                        .limit(keyWordsLimit)
                        .map(KeywordIdDTO::getName)
                        .forEach(keyword -> systemContentBuilder.append(keyword).append("\n"));
            }
        }

        systemContentBuilder.append("\nКатегории без примеров: ");
        categories.stream()
                .skip(verboseCategoriesLimit)
                .map(CategoryDTO::getName)
                .forEach(category -> systemContentBuilder.append(category).append(", "));
        systemContentBuilder.append(".\n\n").append(SYSTEM_BASELINE_ENDING);
        return new Message("system", systemContentBuilder.toString());
    }
}
