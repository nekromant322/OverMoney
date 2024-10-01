package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CategoryRecognizerService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @Autowired
    private LevenshteinCategoryRecognizer levenshteinRecognizer;

    @Autowired
    private ApiCategoryRecognizer apiRecognizer;

    @Value("${recognizer.algorithm.type}")
    protected String recognizerAlgorithmType;

    @Service
    @Slf4j
    @ConditionalOnProperty(name = "recognizer.algorithm.type", havingValue = "levenshtein")
    public static class LevenshteinCategoryRecognizer {
        protected float calculateLevenshteinDistance(String strOne, String strTwo) {
            strOne = strOne.toLowerCase();
            strTwo = strTwo.toLowerCase();
            float maxLength = Integer.max(strOne.length(), strTwo.length());
            if (maxLength > 0) {
                return (maxLength - LevenshteinDistance.getDefaultInstance().apply(strOne, strTwo)) / maxLength;
            }
            return 0.0f;
        }

        public CategoryDTO recognizeCategory(String message, List<CategoryDTO> categories) {
            if (categories.isEmpty()) {
                return null;
            }
            CategoryDTO[] mostSuitableCategory = {categories.get(0)};
            float[] maxLevenshteinDistance = {0};
            categories.forEach(c -> {
                c.getKeywords().add(
                    KeywordIdDTO.builder()
                        .name(c.getName())
                        .build());
            });
            categories.forEach(c -> {
                c.getKeywords().forEach(k -> {
                    float currentValue = calculateLevenshteinDistance(message, k.getName());
                    if (currentValue > maxLevenshteinDistance[0]) {
                        mostSuitableCategory[0] = c;
                        maxLevenshteinDistance[0] = currentValue;
                    }
                });
            });
            return mostSuitableCategory[0];
        }

        protected float calculateAccuracy(CategoryDTO category, String message) {
            return category.getKeywords().stream()
                .map(k -> calculateLevenshteinDistance(message, k.getName()))
                .max(Float::compare)
                .orElse(0.0f);
        }
    }

    @Service
    @Slf4j
    @ConditionalOnProperty(name = "recognizer.algorithm.type", havingValue = "api")
    public static class ApiCategoryRecognizer {

        @Autowired
        private RestTemplate restTemplate;

        @Value("${recognizer.api.url}")
        String recognizerApiUrl;

        public Map<String, Object> recognizeCategoryUsingAPI(String message,
            List<CategoryDTO> categories) {
            String requestJson = new JSONObject()
                .put("model", "llama3.1")
                .put("messages", new JSONArray()
                    .put(new JSONObject()
                        .put("role", "system")
                        .put("content", "Ты помощник по обработке транзакций. " +
                            "Твоя задача - определять категорию финансовой транзакции из предоставленного описания. " +
                            "Категории включают: " + categories.stream().map(CategoryDTO::getName).collect(Collectors.joining(", ")) + ". " +
                            "Ответ должен состоять только из названия категории и оценки уверенности в правильности " +
                            "выбранной категории в значении от 0 до 1"))
                    .put(new JSONObject()
                        .put("role", "user")
                        .put("content", String.format("Помоги определить категорию транзакции с описанием: \"%s\"", message))))
                .put("stream", false)
                .toString();

            String responseJson = restTemplate.postForObject(recognizerApiUrl, requestJson, String.class);

            JSONObject jsonResponse = new JSONObject(responseJson);
            String messageContent = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");
            String[] lines = messageContent.split("\n");
            String categoryName = lines[0].split(":")[1].trim();
            float accuracy = Float.parseFloat(lines[1].split(":")[1].trim());

            Map<String, Object> result = new HashMap<>();
            result.put("categoryName", categoryName);
            result.put("accuracy", accuracy);

            return result;
        }
    }

    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        CategoryDTO suggestedCategory = null;
        float accuracy = 0.0f;

        if ("api".equalsIgnoreCase(recognizerAlgorithmType)) {
            Map<String, Object> result = apiRecognizer.recognizeCategoryUsingAPI(message, categories);
            String categoryName = (String) result.get("categoryName");
            accuracy = (float) result.get("accuracy");
            suggestedCategory = categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElse(null);
        } else if ("levenshtein".equalsIgnoreCase(recognizerAlgorithmType)) {
            suggestedCategory = levenshteinRecognizer.recognizeCategory(message, categories);
            if (suggestedCategory != null) {
                accuracy = levenshteinRecognizer.calculateAccuracy(suggestedCategory, message);
            }
        }

        if (suggestedCategory != null) {
            TransactionDTO transactionDTO = TransactionDTO.builder()
                .accuracy(accuracy)
                .id(transactionId)
                .suggestedCategoryId(suggestedCategory.getId())
                .build();
            orchestratorFeign.editTransaction(transactionDTO);
        } else {
            log.warn("No suggested category for message: {}", message);
        }
    }
}