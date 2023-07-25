package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionXLSXServiceTest {

    @InjectMocks
    private TransactionXLSXService transactionXLSXService;

    @Mock
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private CategoryService categoryService;


    @ParameterizedTest
    @CsvSource({
            "Продукты, Расходы",
            "Транспорт, Расходы",
            "Кафе и рестораны, Расходы",
            "ЖКХ, Расходы",
            "техника, Расходы",
    })
    public void createCategoryTest(String nameCategory, String type) {
        TransactionDTO transactionDTO = generateTestTransactionDTO();
        transactionDTO.setCategoryName(nameCategory);
        Category category = transactionXLSXService.createCategory(transactionDTO, TestFieldsUtil.generateTestAccount());
        assertEquals(category.getName(), StringUtils.capitalize(nameCategory));
        assertEquals(category.getType().getValue(), type);
    }

    @ParameterizedTest
    @CsvSource({
            "пиво, Продукты, 200F",
            "такси, Транспорт, 300F",
            "пончик, Кафе и рестораны, 200F",
            "электричество, ЖКХ, 1500F",
            "колонка, техника, 1000F",
    })
    public void createTransactionTest(String message, String nameCategory, Float amount) {
        Category category = TestFieldsUtil.generateTestCategory();
        TransactionDTO transactionDTO = generateTestTransactionDTO();
        category.setName(nameCategory);
        Set<Category> categories = Set.of(category);
        transactionDTO.setCategoryName(nameCategory);
        transactionDTO.setMessage(message);
        transactionDTO.setAmount(amount);
        when(transactionProcessingService.getMatchingCategory(any(), any())).thenReturn(category);
        Transaction transaction = transactionXLSXService.createTransaction(transactionDTO,
                TestFieldsUtil.generateTestAccount(), categories);
        assertEquals(transaction.getMessage(), message);
        assertEquals(transaction.getCategory().getName(), nameCategory);
        assertEquals(transaction.getAmount(), amount);
    }

    @Test
    public void saveAllCategoriesFromXLSXTest() {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        OverMoneyAccount overMoneyAccount = TestFieldsUtil.generateTestAccount();

        Category category1 = TestFieldsUtil.generateTestCategory();
        category1.setName("Category 1");

        Category category2 = TestFieldsUtil.generateTestCategory();
        category2.setName("Category 2");

        Set<Category> newCategories = new HashSet<>();
        newCategories.add(category2);

        Set<Category> existingCategories = new HashSet<>();
        existingCategories.add(category1);

        overMoneyAccount.setCategories(existingCategories);

        TransactionDTO transaction1 = new TransactionDTO();
        transaction1.setCategoryName("Category 1");
        transactionDTOS.add(transaction1);

        TransactionDTO transaction2 = new TransactionDTO();
        transaction2.setCategoryName("Category 2");
        transactionDTOS.add(transaction2);

        transactionXLSXService.saveAllCategoriesFromXLSX(transactionDTOS, overMoneyAccount);

        verify(categoryService, times(1)).saveAllCategories(newCategories);
    }


    private TransactionDTO generateTestTransactionDTO() {
        return TransactionDTO.builder()
                .categoryName("Продукты")
                .message("продукты")
                .amount(500F)
                .date(LocalDateTime.now())
                .build();
    }
}
