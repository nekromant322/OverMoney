package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.llm.Message;
import com.override.recognizer_service.llm.LLMResponseDTO;
import com.override.recognizer_service.service.category.LlamaApiCategoryRecognizer;
import com.override.recognizer_service.service.category.LevenshteinCategoryRecognizer;
import com.override.recognizer_service.service.category.RecognizerResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoryRecognizerServiceTests {

    @InjectMocks
    private LevenshteinCategoryRecognizer levenshteinRecognizer;

    @Mock
    private LlamaApiCategoryRecognizer llamaApiCategoryRecognizer;

    private float minAccuracy;

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
        List<CategoryDTO> categories = List.of(categoryWithBeer);

        RecognizerResult result = levenshteinRecognizer.recognizeCategoryAndAccuracy(message, categories);

        Assertions.assertEquals(categoryWithBeer.getName(), result.getCategory().getName());
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
        List<CategoryDTO> categories = List.of(categoryWithBeer, categoryWithBlank);

        RecognizerResult result = levenshteinRecognizer.recognizeCategoryAndAccuracy(message, categories);

        Assertions.assertEquals(categoryWithBeer.getName(), result.getCategory().getName());
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
        List<CategoryDTO> categories = List.of(categoryWithLupa, categoryWithPupa);

        RecognizerResult result = levenshteinRecognizer.recognizeCategoryAndAccuracy(message, categories);

        Assertions.assertEquals(categoryWithPupa.getName(), result.getCategory().getName());
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
        List<CategoryDTO> categories = List.of(categoryWithAnalgin);

        RecognizerResult result = levenshteinRecognizer.recognizeCategoryAndAccuracy(message, categories);
        float accuracy = result.getAccuracy();
        CategoryDTO recognizedCategory = result.getCategory();

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
        List<CategoryDTO> categories = List.of(categoryWithApple);

        RecognizerResult result = levenshteinRecognizer.recognizeCategoryAndAccuracy(message, categories);

        Assertions.assertTrue(result.getAccuracy() > minAccuracy);
        Assertions.assertEquals(categoryWithApple.getName(), result.getCategory().getName());
    }

    @Test
    public void testLLM() {
        String message = "банка пива 300";
        String categoryName = "продукты";
        float accuracy = minAccuracy;

        String mockContent = categoryName + ", " + accuracy;
        Message mockMessage = new Message();
        mockMessage.setContent(mockContent);
        LLMResponseDTO mockResponse = new LLMResponseDTO();
        mockResponse.setMessage(mockMessage);
        List<CategoryDTO> categories = new ArrayList<>();
        categories.add(CategoryDTO.builder().name(categoryName).build());

        when(llamaApiCategoryRecognizer.recognizeCategoryUsingAPI(message, categories)).thenReturn(
            mockResponse);

        LLMResponseDTO response = llamaApiCategoryRecognizer.recognizeCategoryUsingAPI(message,
            categories);

        Assertions.assertEquals(categoryName, response.getCategoryName());
        Assertions.assertEquals(accuracy, response.getAccuracy(),
            0.001);
    }
}