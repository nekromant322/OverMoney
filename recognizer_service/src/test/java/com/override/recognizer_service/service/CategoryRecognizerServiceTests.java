package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-test.yml")
public class CategoryRecognizerServiceTests {
    @InjectMocks
    private CategoryRecognizerService categoryRecognizerService;

    private float minAccuracy;

    @BeforeEach
    public void setup() {
        minAccuracy = 0.7f;
    }

    @Mock
    private OrchestratorFeign orchestratorFeign;

    @Test
    public void returnCorrectCategoryWhenKeywordMatched() {
        final KeywordIdDTO keywordBeer = KeywordIdDTO.builder()
                .accountId(1L)
                .name("пиво")
                .build();
        List<KeywordIdDTO> listOfKeywords = new ArrayList<>();
        listOfKeywords.add(keywordBeer);
        final CategoryDTO categoryWithBeer = CategoryDTO.builder()
                .keywords(listOfKeywords)
                .name("Категория с пивом")
                .build();
        final String message = "пиво";
        Assertions.assertEquals(categoryRecognizerService.recognizeCategory(message, List.of(categoryWithBeer)).getName(),
                categoryWithBeer.getName());
    }

    @Test
    public void returnAnotherWhenOneIsBlank() {
        final KeywordIdDTO keywordBeer = KeywordIdDTO.builder()
                .accountId(1L)
                .name("пиво")
                .build();
        final KeywordIdDTO keywordBlank = KeywordIdDTO.builder()
                .accountId(1L)
                .name("")
                .build();

        List<KeywordIdDTO> listOfKeywordBeer = new ArrayList<>();
        listOfKeywordBeer.add(keywordBeer);
        List<KeywordIdDTO> listOfKeywordBlank = new ArrayList<>();
        listOfKeywordBlank.add(keywordBlank);

        final CategoryDTO categoryWithBeer = CategoryDTO.builder()
                .keywords(listOfKeywordBeer)
                .name("Категория с пивом")
                .build();
        final CategoryDTO categoryWithBlank = CategoryDTO.builder()
                .keywords(listOfKeywordBlank)
                .name("Категория без ключевых слов")
                .build();
        final String message = "молоко";
        Assertions.assertEquals(categoryRecognizerService
                        .recognizeCategory(message, List.of(categoryWithBeer, categoryWithBlank)).getName(),
                categoryWithBeer.getName());
    }

    @Test
    public void returnCorrectCategoryWhenMisspell() {
        final KeywordIdDTO keywordLupa = KeywordIdDTO.builder()
                .accountId(1L)
                .name("лупа")
                .build();
        final KeywordIdDTO keywordPupa = KeywordIdDTO.builder()
                .accountId(1L)
                .name("пупа")
                .build();
        List<KeywordIdDTO> listOfKeywordPupa = new ArrayList<>();
        listOfKeywordPupa.add(keywordPupa);
        List<KeywordIdDTO> listOfKeywordLupa = new ArrayList<>();
        listOfKeywordLupa.add(keywordLupa);

        final CategoryDTO categoryWithLupa = CategoryDTO.builder()
                .keywords(listOfKeywordLupa)
                .name("Категория с Лупой")
                .build();
        final CategoryDTO categoryWithPupa = CategoryDTO.builder()
                .keywords(listOfKeywordPupa)
                .name("Категория с Пупой")
                .build();
        final String message = "пупв";
        Assertions.assertEquals(categoryRecognizerService
                        .recognizeCategory(message, List.of(categoryWithLupa, categoryWithPupa)).getName(),
                categoryWithPupa.getName());
    }
    @Test
    public void doNotSuggestCategoryWhenAccuracyIsLow() {
        final KeywordIdDTO keywordAnalgin = KeywordIdDTO.builder()
            .accountId(1L)
            .name("анальгин")
            .build();
        List<KeywordIdDTO> listOfKeywords = new ArrayList<>();
        listOfKeywords.add(keywordAnalgin);
        final CategoryDTO categoryWithAnalgin = CategoryDTO.builder()
            .keywords(listOfKeywords)
            .name("Категория с анальгином")
            .build();
        final String message = "апельсин";
        CategoryDTO recognizedCategory = categoryRecognizerService.recognizeCategory(message, List.of(categoryWithAnalgin));
        float accuracy = categoryRecognizerService.calculateLevenshteinDistance(message, keywordAnalgin.getName());
        Assertions.assertNull(recognizedCategory == null ? null : (accuracy < minAccuracy ? null : recognizedCategory));
    }

    @Test
    public void suggestCategoryWhenAccuracyIsHigh() {
        final KeywordIdDTO keywordApple = KeywordIdDTO.builder()
            .accountId(1L)
            .name("яблоко")
            .build();
        List<KeywordIdDTO> listOfKeywords = new ArrayList<>();
        listOfKeywords.add(keywordApple);
        final CategoryDTO categoryWithApple = CategoryDTO.builder()
            .keywords(listOfKeywords)
            .name("Категория с яблоком")
            .build();
        String message = "яблоки";
        UUID transactionId = UUID.randomUUID();
        float accuracy = categoryRecognizerService.calculateLevenshteinDistance(message,
            keywordApple.getName());
        Assertions.assertTrue(accuracy > minAccuracy);

      categoryRecognizerService.sendTransactionWithSuggestedCategory(message,
          List.of(categoryWithApple), transactionId);

      verify(orchestratorFeign).editTransaction(TransactionDTO.builder()
          .suggestedCategoryId(categoryWithApple.getId())
          .accuracy(accuracy)
          .id(transactionId).build());
    }
}