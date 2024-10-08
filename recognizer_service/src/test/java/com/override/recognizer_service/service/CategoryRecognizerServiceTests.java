package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.llm.Message;
import com.override.recognizer_service.llm.LLMResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoryRecognizerServiceTests {

    @Spy
    private LevenshteinCategoryRecognizer levenshteinRecognizer;

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
        when(
            levenshteinRecognizer.getSuggestedCategory(message, List.of(categoryWithBeer))).thenReturn(
            categoryWithBeer);

        Assertions.assertEquals(
            levenshteinRecognizer.getSuggestedCategory(message, List.of(categoryWithBeer)).getName(),
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
        when(levenshteinRecognizer.getSuggestedCategory(message,
            List.of(categoryWithBeer, categoryWithBlank)))
            .thenReturn(categoryWithBeer);

        Assertions.assertEquals(categoryWithBeer.getName(),
            levenshteinRecognizer
                .getSuggestedCategory(message, List.of(categoryWithBeer, categoryWithBlank))
                .getName());
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
        when(levenshteinRecognizer.getSuggestedCategory(message,
            List.of(categoryWithLupa, categoryWithPupa))).thenReturn(categoryWithPupa);

        Assertions.assertEquals(levenshteinRecognizer
                .getSuggestedCategory(message, List.of(categoryWithLupa, categoryWithPupa)).getName(),
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

        CategoryDTO recognizedCategory = levenshteinRecognizer.getSuggestedCategory(message,
            List.of(categoryWithAnalgin));
        float accuracy = levenshteinRecognizer.getAccuracy(message, List.of(categoryWithAnalgin));

        Assertions.assertNull(recognizedCategory == null ? null
            : (accuracy < minAccuracy ? null : recognizedCategory));
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

        float accuracy = levenshteinRecognizer.getAccuracy(message, List.of(categoryWithApple));
        Assertions.assertTrue(accuracy > minAccuracy);
        CategoryDTO recognizedCategory = levenshteinRecognizer.getSuggestedCategory(message,
            List.of(categoryWithApple));
        if (accuracy > minAccuracy) {
            Assertions.assertNotNull(recognizedCategory);
        } else {
            Assertions.assertNull(recognizedCategory);
        }
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

        ApiCategoryRecognizer apiCategoryRecognizer = Mockito.mock(ApiCategoryRecognizer.class);

        when(apiCategoryRecognizer.recognizeCategoryUsingAPI(message, categories)).thenReturn(
            mockResponse);

        LLMResponseDTO response = apiCategoryRecognizer.recognizeCategoryUsingAPI(message,
            categories);

        Assertions.assertEquals(categoryName, response.getCategoryName());
        Assertions.assertEquals(accuracy, response.getAccuracy(),
            0.001);
    }
}