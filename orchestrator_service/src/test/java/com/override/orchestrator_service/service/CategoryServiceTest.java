package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.List;
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
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private TransactionRepository transactionRepository;

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

    @Test
    public void findCategoriesListByTypeWhenTypeExpense() throws InstanceNotFoundException {
        final Category categoryExpense1 = TestFieldsUtil.generateTestCategory();
        final Category categoryExpense2 = TestFieldsUtil.generateTestCategory();
        categoryExpense2.setId(12346L);
        categoryExpense2.setName("Тест2");

        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        List<Category> categoryList = List.of(categoryExpense1, categoryExpense2);
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(account);
        when(categoryRepository.findAllByTypeAndAccId(account.getId(), Type.EXPENSE)).thenReturn(categoryList);

        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByType(account.getId(), Type.EXPENSE);
        for (CategoryDTO categoryDTO : categoryDTOList) {
            Assertions.assertEquals(categoryDTO.getType(), Type.EXPENSE);
        }
    }

    @Test
    public void findCategoriesListByTypeWhenTypeIncome() throws InstanceNotFoundException {
        final Category categoryExpense1 = TestFieldsUtil.generateTestCategory();
        categoryExpense1.setType(Type.INCOME);

        final Category categoryExpense2 = TestFieldsUtil.generateTestCategory();
        categoryExpense2.setId(12346L);
        categoryExpense2.setName("Тест2");
        categoryExpense2.setType(Type.INCOME);

        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        List<Category> categoryList = List.of(categoryExpense1, categoryExpense2);
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(account);
        when(categoryRepository.findAllByTypeAndAccId(account.getId(), Type.INCOME)).thenReturn(categoryList);

        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByType(account.getId(), Type.INCOME);
        for (CategoryDTO categoryDTO : categoryDTOList) {
            Assertions.assertEquals(categoryDTO.getType(), Type.INCOME);
        }
    }

    @Test
    public void mergeCategoryTest() {
        final Category categoryToChange = TestFieldsUtil.generateTestCategory();
        final Category categoryToMerge = TestFieldsUtil.generateTestCategory();
        categoryToMerge.setId(12346L);
        categoryToMerge.setName("Тест2");

        categoryService.mergeCategory(categoryToMerge.getId(), categoryToChange.getId());

        verify(keywordRepository, times(1)).updateCategoryId(categoryToMerge.getId(), categoryToChange.getId());
        verify(transactionRepository, times(1)).updateCategoryId(categoryToMerge.getId(), categoryToChange.getId());
        verify(categoryRepository, times(1)).deleteById(categoryToMerge.getId());
    }

    @Test
    public void updateCategoryTest() throws InstanceNotFoundException {
        final CategoryDTO category = TestFieldsUtil.generateTestCategoryDTO();
        when(categoryMapper.mapCategoryDTOToCategory(any(),any())).thenReturn(TestFieldsUtil.generateTestCategory());
        categoryService.updateCategoryForAcc(any(), category);
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    public void deleteKeywordTest() {
        final CategoryDTO category = TestFieldsUtil.generateTestCategoryDTO();
        categoryService.deleteKeyword(category.getId(), category.getKeywords().get(0));
        verify(keywordRepository, times(1)).deleteKeywordByCategoryIdAndValue
                (category.getId(), category.getKeywords().get(0));
    }

}
