package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.*;

@Service
public class BackupUserDataService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private OverMoneyAccountService overMoneyAccountService;
    @Autowired
    TransactionProcessingService transactionProcessingService;

    public BackupUserDataDTO createBackupUserData(Long telegramId) throws InstanceNotFoundException {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByUserId(telegramId);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByUserId(telegramId);

        return BackupUserDataDTO.builder()
                .categoryDTOList(categoryDTOList)
                .transactionDTOList(transactionDTOList).build();
    }

    public void writingDataFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<Transaction> transactionList = createTransactionsFromBackupFile(backupUserDataDTO, telegramId);
        transactionService.saveAllTransactions(transactionList);
    }

    public List<Transaction> createTransactionsFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<TransactionDTO> transactionDTOList = backupUserDataDTO.getTransactionDTOList();
        List<CategoryDTO> categoryDTOList = backupUserDataDTO.getCategoryDTOList();
        List<Transaction> transactionList = new ArrayList<>();
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getNewAccount(telegramId);
        Long accountId = overMoneyAccount.getId();
        transactionDTOList.stream()
                .filter(transactionDTO -> transactionDTO.getCategoryName() == null)
                .forEach(transactionDTO -> transactionDTO.setCategoryName("Нераспознанное"));
        Set<Category> categorySet = new HashSet<>();
        categoryDTOList.forEach(categoryDTO -> categorySet.add(categoryMapper.mapCategoryDTOToCategory(categoryDTO,
                overMoneyAccount)));

        overMoneyAccountService.deletingAllTransactionsCategoriesKeywordsByAccountId(accountId);
        categoryService.saveAllCategories(categorySet);

        for (TransactionDTO transactionDTO : transactionDTOList) {
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setMessage(transactionDTO.getMessage());
            Optional<CategoryDTO> categoryDTO = categoryService.findCategoryDTOByNameFromList(categoryDTOList,
                    transactionDTO.getCategoryName());
            Category category = transactionProcessingService.getMatchingCategory(categorySet, transactionDTO.getCategoryName());
            categoryDTO.ifPresent(dto -> keywordService.setKeywordsFromCategoryDTO(dto, category, accountId));
            transaction.setCategory(category);
            transaction.setAccount(overMoneyAccount);
            transaction.setDate(transactionDTO.getDate());
            transaction.setTelegramUserId(telegramId);

            transactionList.add(transaction);
        }
        return transactionList;
    }
}


