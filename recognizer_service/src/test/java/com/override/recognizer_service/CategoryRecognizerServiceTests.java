package com.override.recognizer_service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.service.CategoryRecognizerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
        final CategoryDTO categoryWithBeer = CategoryDTO.builder()
                .keywords(List.of(keywordBeer))
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
        final CategoryDTO categoryWithBeer = CategoryDTO.builder()
                .keywords(List.of(keywordBeer))
                .name("Категория с пивом")
                .build();
        final CategoryDTO categoryWithBlank = CategoryDTO.builder()
                .keywords(List.of(keywordBlank))
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
        final CategoryDTO categoryWithLupa = CategoryDTO.builder()
                .keywords(List.of(keywordLupa))
                .name("Категория с Лупой")
                .build();
        final CategoryDTO categoryWithPupa = CategoryDTO.builder()
                .keywords(List.of(keywordPupa))
                .name("Категория с Пупой")
                .build();
        final String message = "пупв";
        Assertions.assertEquals(categoryRecognizerService
                        .recognizeCategory(message, List.of(categoryWithLupa, categoryWithPupa)).getName(),
                categoryWithPupa.getName());
    }
}
