package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OverMoneyAccountService overMoneyAccountService;

    @Test
    public void getCategoryByIdThrowExceptionWhenCategoryNotFound() {
        final Category category = new Category();
        category.setId(12345L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.getCategoryById(category.getId()));
    }

    @Test
    public void getCategoryByIdReturnCategoryWhenCategoryFound() {
        final Category category = new Category();
        category.setId(12345L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Category expectation = categoryService.getCategoryById(category.getId());

        assertEquals(expectation, category);
    }

    @Test
    public void saveCategoryForAccTest() throws InstanceNotFoundException {
        final CategoryDTO categoryDTO = TestFieldsUtil.generateTestCategoryDTO();
        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        when(overMoneyAccountService.getAccountByUserId(account.getId()))
                .thenReturn(account);

        when(categoryMapper.mapCategoryDTOToCategory(categoryDTO, account))
                .thenReturn(TestFieldsUtil.generateTestCategory());

        categoryService.saveCategoryForAcc(TestFieldsUtil.generateTestAccount().getId(), categoryDTO);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
