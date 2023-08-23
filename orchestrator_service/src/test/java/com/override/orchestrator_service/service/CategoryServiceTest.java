package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.MergeCategoryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.config.DefaultCategoryProperties;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.mapper.AccountMapper;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.KeywordId;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
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
import java.util.*;

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

    @Mock
    private DefaultCategoryProperties defaultCategoryProperties;
    @Mock
    private UserService userService;

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

        when(categoryMapper.mapCategoryDTOToCategory(any(), any()))
                .thenReturn(TestFieldsUtil.generateTestCategory());

        categoryService.saveCategoryForAcc(TestFieldsUtil.generateTestAccount().getId(), categoryDTO);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void saveAllCategoriesTest() {
        Set<Category> categories = new HashSet<>();
        Category category1 = TestFieldsUtil.generateTestCategory();
        Category category2 = TestFieldsUtil.generateTestCategory();
        category2.setName("ЖКХ");
        categories.add(category1);
        categories.add(category2);
        categoryService.saveAllCategories(categories);
        verify(categoryRepository, times(1)).saveAll(categories);
    }

    @Test
    public void testGetCategoriesByUserId() {
        OverMoneyAccount overMoneyAccount = TestFieldsUtil.generateTestAccountNoCategory();
        Category category1 = TestFieldsUtil.generateTestCategory();
        category1.setAccount(overMoneyAccount);
        Category category2 = TestFieldsUtil.generateTestCategory();
        category2.setName("Category 2");
        category2.setAccount(overMoneyAccount);
        Set<Category> categories = Set.of(category1, category2);

        when(categoryRepository.findAllByAccount_Id(overMoneyAccount.getId())).thenReturn(categories);
        Set<Category> result = categoryService.getCategoriesByUserId(overMoneyAccount.getId());
        assertEquals(categories.size(), result.size());
    }

    @Test
    public void findCategoriesListByTypeWhenTypeExpense() throws InstanceNotFoundException {
        final Category categoryExpense1 = TestFieldsUtil.generateTestCategory();
        final Category categoryExpense2 = TestFieldsUtil.generateTestCategory();
        categoryExpense2.setId(12346L);
        categoryExpense2.setName("Тест2");

        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        List<Category> categoryList = List.of(categoryExpense1, categoryExpense2);
        when(categoryRepository.findAllByTypeAndAccId(account.getId(), Type.EXPENSE)).thenReturn(categoryList);
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(account);

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
        when(categoryRepository.findAllByTypeAndAccId(account.getId(), Type.INCOME)).thenReturn(categoryList);
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(account);

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
        MergeCategoryDTO categoryIDsTest =
                MergeCategoryDTO.builder()
                        .categoryToMergeId(categoryToMerge.getId())
                        .categoryToChangeId(categoryToChange.getId())
                        .build();

        categoryService.mergeCategory(categoryIDsTest);

        verify(keywordRepository, times(1)).updateCategoryId(categoryToMerge.getId(), categoryToChange.getId());
        verify(transactionRepository, times(1)).updateCategoryId(categoryToMerge.getId(), categoryToChange.getId());
        verify(categoryRepository, times(1)).deleteById(categoryToMerge.getId());
    }

    @Test
    public void updateCategoryTest() throws InstanceNotFoundException {
        final CategoryDTO category = TestFieldsUtil.generateTestCategoryDTO();
        when(categoryMapper.mapCategoryDTOToCategory(any(), any())).thenReturn(TestFieldsUtil.generateTestCategory());
        categoryService.updateCategoryForAcc(any(), category);
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    public void deleteKeywordTest() {
        final KeywordId keywordId = new KeywordId();
        keywordId.setName("Тест");
        keywordId.setAccountId(123L);
        categoryService.deleteKeyword(keywordId);
        verify(keywordRepository, times(1)).deleteByKeywordId(keywordId);
    }

    @Test
    public void setDefaultCategoryForAccTest() throws InstanceNotFoundException {
        OverMoneyAccount accountTest = TestFieldsUtil.generateTestAccount();
        when(overMoneyAccountService.getAccountByUserId(any())).thenReturn(accountTest);

        DefaultCategoryProperties.DefaultCategory category1 = new DefaultCategoryProperties.DefaultCategory();
        DefaultCategoryProperties.DefaultCategory category2 = new DefaultCategoryProperties.DefaultCategory();
        DefaultCategoryProperties.DefaultCategory category3 = new DefaultCategoryProperties.DefaultCategory();

        List<DefaultCategoryProperties.DefaultCategory> categories = List.of(category1, category2, category3);
        when(defaultCategoryProperties.getCategories()).thenReturn(categories);
        categoryService.setDefaultCategoryForAccount(accountTest.getId());
        verify(categoryRepository, times(3)).save(any());
    }

    @Test
    public void saveAllCategoryTest() {
        List<Category> categoryList = new ArrayList<>();
        Category category = TestFieldsUtil.generateTestCategory();
        categoryList.add(category);

        categoryRepository.saveAll(categoryList);

        verify(categoryRepository, times(1)).saveAll(categoryList);
    }

    @Test
    public void findCategoryDTOByNameFromListTestIsPresent() {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        categoryDTOList.add(TestFieldsUtil.generateTestCategoryDTO());
        String categoryDTOName = "продукты";
        Long id = 12345L;
        Type type = Type.EXPENSE;

        categoryService.findCategoryDTOByNameFromList(categoryDTOList, categoryDTOName);

        Assertions.assertEquals(categoryDTOList.get(0).getName(), categoryDTOName);
        Assertions.assertEquals(categoryDTOList.get(0).getId(), id);
        Assertions.assertEquals(categoryDTOList.get(0).getType(), type);
    }

    @Test
    public void findCategoryDTOByNameFromListTestIsEmpty() {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        categoryDTOList.add(TestFieldsUtil.generateTestCategoryDTO());
        String categoryDTOName = "грибы";

        Assertions.assertTrue(categoryService.findCategoryDTOByNameFromList(categoryDTOList, categoryDTOName).isEmpty());
    }
}
