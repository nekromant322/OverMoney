package com.override.orchestrator_service.service;

import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void getCategoryByIdThrowExceptionWhenCategoryNotFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.getCategoryById(category.getId()));
    }

    @Test
    public void getCategoryByIdReturnCategoryWhenCategoryFound() {
        final Category category = new Category();
        category.setId(UUID.randomUUID());

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Category expectation = categoryService.getCategoryById(category.getId());

        assertEquals(expectation, category);
    }
}
