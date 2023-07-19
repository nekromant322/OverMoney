package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
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
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TransactionService transactionService;

    @Test
    public void createBackupUserDataTest() throws InstanceNotFoundException {
        Long telegramId = 1L;
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByUserId(telegramId);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByUserId(telegramId);

        verify(categoryService, times(1)).findCategoriesListByUserId(telegramId);
        verify(transactionService, times(1)).findAlltransactionDTOForAcountByUserId(telegramId);
    }

    @Test
    public void writingDataFromBackupFileTest() {
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transactionList.add(transaction);

        transactionRepository.saveAll(transactionList);
        verify(transactionRepository, times(1)).saveAll(any());
    }
}
