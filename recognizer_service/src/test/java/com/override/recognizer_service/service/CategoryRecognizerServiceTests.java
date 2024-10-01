package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.recognizer_service.feign.OrchestratorFeign;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryRecognizerServiceTests {

    @InjectMocks
    private CategoryRecognizerService categoryRecognizerService;

    @Mock
    private CategoryRecognizerService.LevenshteinCategoryRecognizer levenshteinCategoryRecognizer;

    @Mock
    private CategoryRecognizerService.ApiCategoryRecognizer apiRecognizer;

    private float minAccuracy;

    @Mock
    private OrchestratorFeign orchestratorFeign;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        minAccuracy = 0.7f;
    }

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
        when(levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithBeer))).thenReturn(categoryWithBeer);
        Assertions.assertEquals(levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithBeer)).getName(),
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
        when(levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithBeer, categoryWithBlank))).thenReturn(categoryWithBeer);
        Assertions.assertEquals(levenshteinCategoryRecognizer
                .recognizeCategory(message, List.of(categoryWithBeer, categoryWithBlank)).getName(),
            categoryWithBeer.getName());
    }

    @Test
    public void returnCorrectCategoryWhenIncorrectlySpelled() {
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
        when(levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithLupa, categoryWithPupa))).thenReturn(categoryWithPupa);
        Assertions.assertEquals(levenshteinCategoryRecognizer
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

        when(levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithAnalgin))).thenReturn(null);
        when(levenshteinCategoryRecognizer.calculateLevenshteinDistance(message, keywordAnalgin.getName())).thenReturn(0.5f);

        CategoryDTO recognizedCategory = levenshteinCategoryRecognizer.recognizeCategory(message, List.of(categoryWithAnalgin));
        float accuracy = levenshteinCategoryRecognizer.calculateLevenshteinDistance(message, keywordAnalgin.getName());

        Assertions.assertTrue(recognizedCategory == null || accuracy < minAccuracy);
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
            .id(1L)
            .build();
        String message = "яблоки";

        float accuracy = 0.7f;

        UUID transactionId = UUID.randomUUID();
        when(levenshteinCategoryRecognizer.calculateLevenshteinDistance(message, keywordApple.getName()))
            .thenReturn(accuracy);

        categoryRecognizerService.sendTransactionWithSuggestedCategory(message, List.of(categoryWithApple), transactionId);

        verify(orchestratorFeign).editTransaction(TransactionDTO.builder()
            .suggestedCategoryId(categoryWithApple.getId())
            .accuracy(accuracy)
            .id(transactionId).build());
    }
    @Test
    public void testApiRecognizer() {
        final KeywordIdDTO keywordFood = KeywordIdDTO.builder()
            .accountId(1L)
            .name("картофель")
            .build();
        List<KeywordIdDTO> listOfKeywords = new ArrayList<>();
        listOfKeywords.add(keywordFood);

        final CategoryDTO categoryWithFood = CategoryDTO.builder()
            .keywords(listOfKeywords)
            .name("Продукты")
            .id(2L)
            .build();
        String message = "Картофель";

        String apiResponse = "{\"choices\": [{\"text\": \"Категория: Продукты\\nУверенность: 0.7\"}]}";
        when(restTemplate.postForObject(eq(apiRecognizer.recognizerApiUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(apiResponse);

        Map<String, Object> result = apiRecognizer.recognizeCategoryUsingAPI(message, List.of(categoryWithFood));

        Assertions.assertEquals("Продукты", result.get("categoryName"));
        Assertions.assertEquals(0.7f, result.get("accuracy"));
    }
}