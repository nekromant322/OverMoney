package com.override.recognizer_service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.recognizer_service.service.CategoryRecognizerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryRecognizerServiceTests {
    @InjectMocks
    private CategoryRecognizerService categoryRecognizerService;

    @Test
    public void returnONEWhen() {
        //
    }
}
