package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.event.CategoryRecognitionEvent;
import com.override.orchestrator_service.event.listeners.CategoryRecognitionListener;
import com.override.orchestrator_service.feign.RecognizerFeign;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRecognitionListenerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private RecognizerFeign recognizerFeign;

    @InjectMocks
    private CategoryRecognitionListener listener;

    @Test
    void onCategoryRecognition_shouldCallRecognizer_whenCategoriesExist() throws InstanceNotFoundException {
        UUID uuid = UUID.randomUUID();
        CategoryRecognitionEvent event = new CategoryRecognitionEvent(
                uuid,
                "пиво 300",
                777L
        );


        List<CategoryDTO> categories = List.of(new CategoryDTO());

        when(categoryService.findCategoriesListByUserId(777L))
                .thenReturn(categories);

        listener.onCategoryRecognition(event);

        verify(categoryService).findCategoriesListByUserId(777L);
        verify(recognizerFeign).recognizeCategory(
                "пиво 300",
                uuid,
                categories
        );
    }

    @Test
    void onCategoryRecognition_shouldNotCallRecognizer_whenCategoriesEmpty() throws InstanceNotFoundException {
        UUID uuid = UUID.randomUUID();
        CategoryRecognitionEvent event = new CategoryRecognitionEvent(
                uuid,
                "пиво 300",
                777L
        );

        when(categoryService.findCategoriesListByUserId(777L))
                .thenReturn(List.of());

        listener.onCategoryRecognition(event);

        verify(categoryService).findCategoriesListByUserId(777L);
        verifyNoInteractions(recognizerFeign);
    }

    @Test
    void onCategoryRecognition_shouldHandleFeignException() throws InstanceNotFoundException {
        UUID uuid = UUID.randomUUID();
        CategoryRecognitionEvent event = new CategoryRecognitionEvent(
                uuid,
                "пиво 300",
                777L
        );

        List<CategoryDTO> categories = List.of(new CategoryDTO());

        when(categoryService.findCategoriesListByUserId(777L))
                .thenReturn(categories);

        doThrow(mock(FeignException.class))
                .when(recognizerFeign)
                .recognizeCategory(
                        "пиво 300",
                        uuid,
                        categories
                );

        listener.onCategoryRecognition(event);

        verify(recognizerFeign).recognizeCategory(
                "пиво 300",
                uuid,
                categories
        );
    }

    @Test
    void onCategoryRecognition_shouldHandleUnexpectedException() throws InstanceNotFoundException {
        UUID uuid = UUID.randomUUID();
        CategoryRecognitionEvent event = new CategoryRecognitionEvent(
                uuid,
                "пиво 300",
                777L
        );

        when(categoryService.findCategoriesListByUserId(777L))
                .thenThrow(new RuntimeException("DB error"));

        listener.onCategoryRecognition(event);

        verify(categoryService).findCategoriesListByUserId(777L);
        verifyNoInteractions(recognizerFeign);
    }
}
