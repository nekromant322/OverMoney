package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class BackupUserDataService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionService transactionService;

    public BackupUserDataDTO createBackupUserData(Long telegramId) throws InstanceNotFoundException {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByUserId(telegramId);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByUserId(telegramId);

        return BackupUserDataDTO.builder()
                .categoryDTOList(categoryDTOList)
                .transactionDTOList(transactionDTOList).build();
    }
}
