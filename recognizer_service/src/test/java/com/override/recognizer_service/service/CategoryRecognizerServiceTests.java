package com.override.recognizer_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.service.CategoryRecognizerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CategoryRecognizerServiceTests {
    @InjectMocks
    private CategoryRecognizerService categoryRecognizerService;

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
}
