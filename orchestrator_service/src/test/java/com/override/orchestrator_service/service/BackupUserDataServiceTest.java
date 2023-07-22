package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackupUserDataServiceTest {

    @InjectMocks
    private BackupUserDataService backupUserDataService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private OverMoneyAccountService overMoneyAccountService;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TransactionProcessingService transactionProcessingService;

    @Test
    public void createBackupUserDataTest() throws InstanceNotFoundException {
        BackupUserDataDTO backupUserDataDTO = TestFieldsUtil.generateTestBackupUserDataDTO();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        CategoryDTO categoryDTO = TestFieldsUtil.generateTestCategoryDTO();
        categoryDTOList.add(categoryDTO);
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        TransactionDTO transactionDTO = TestFieldsUtil.generateTestTransactionDTO();
        transactionDTOList.add(transactionDTO);

        when(categoryService.findCategoriesListByUserId(any())).thenReturn(categoryDTOList);
        when(transactionService.findAlltransactionDTOForAcountByUserId(any())).thenReturn(transactionDTOList);
        backupUserDataService.createBackupUserData(any());

        Assertions.assertEquals(backupUserDataDTO.getTransactionDTOList().size(), transactionDTOList.size());
        Assertions.assertEquals(backupUserDataDTO.getCategoryDTOList().size(), categoryDTOList.size());
        Assertions.assertEquals(backupUserDataDTO.getTransactionDTOList().get(0).getMessage(), transactionDTOList.get(0).getMessage());
        Assertions.assertEquals(backupUserDataDTO.getCategoryDTOList().get(0).getId(), categoryDTOList.get(0).getId());
    }

    @Test
    public void writingDataFromBackupFileTest() {
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transactionList.add(transaction);

        transactionService.saveAllTransactions(transactionList);

        verify(transactionService, times(1)).saveAllTransactions(transactionList);
    }

    @Test
    public void createTransactionsFromBackupFileTest() {
        BackupUserDataDTO backupUserDataDTO = TestFieldsUtil.generateTestBackupUserDataDTO();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        CategoryDTO categoryDTO = TestFieldsUtil.generateTestCategoryDTO();
        categoryDTOList.add(categoryDTO);
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        TransactionDTO transactionDTO = TestFieldsUtil.generateTestTransactionDTO();
        transactionDTOList.add(transactionDTO);
        backupUserDataDTO.setTransactionDTOList(transactionDTOList);
        backupUserDataDTO.setCategoryDTOList(categoryDTOList);
        OverMoneyAccount overMoneyAccount = TestFieldsUtil.generateTestAccount();
        Category category = TestFieldsUtil.generateTestCategory();

        when(overMoneyAccountService.getNewAccount(any())).thenReturn(overMoneyAccount);
        when(categoryMapper.mapCategoryDTOToCategory(any(), any())).thenReturn(category);
        when(transactionProcessingService.getMatchingCategory(any(), any())).thenReturn(category);
        List<Transaction> transactionList = backupUserDataService.createTransactionsFromBackupFile(backupUserDataDTO, any());

        verify(overMoneyAccountService, times(1)).deletingAllTransactionsCategoriesKeywordsByAccountId(any());
        verify(categoryService, times(1)).saveAllCategories(any());
        Assertions.assertEquals(transactionList.size(), transactionDTOList.size());
        Assertions.assertEquals(transactionList.get(0).getMessage(), transactionDTOList.get(0).getMessage());
        Assertions.assertEquals(transactionList.get(0).getAmount(), transactionDTOList.get(0).getAmount());
    }


}

