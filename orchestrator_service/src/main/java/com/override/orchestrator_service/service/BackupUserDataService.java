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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public BackupUserDataDTO createBackupUserData(Long telegramId) throws InstanceNotFoundException {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByUserId(telegramId);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByUserId(telegramId);

        return BackupUserDataDTO.builder()
                .categoryDTOList(categoryDTOList)
                .transactionDTOList(transactionDTOList).build();
    }

    public void writingDataFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<Transaction> transactionList = createTransactionsFromBackupFile(backupUserDataDTO, telegramId);
        transactionService.saveTransactionsList(transactionList);
    }

    private List<Transaction> createTransactionsFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<TransactionDTO> transactionDTOList = backupUserDataDTO.getTransactionDTOList();
        List<CategoryDTO> categoryDTOList = backupUserDataDTO.getCategoryDTOList();
        List<Transaction> transactionList = new ArrayList<>();
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getNewAccount(telegramId);
        Long accountId = overMoneyAccount.getId();
        transactionDTOList.stream()
                .filter(transactionDTO -> transactionDTO.getCategoryName() == null)
                .forEach(transactionDTO -> transactionDTO.setCategoryName("Нераспознанное"));
        List<Category> categoryList = new ArrayList<>();
        categoryDTOList.forEach(categoryDTO -> categoryList.add(categoryMapper.mapCategoryDTOToCategory(categoryDTO,
                overMoneyAccount)));

        if (categoryDTOList.size() == categoryList.size()) {
            categoryService.deletingAndOverwritingCategories(categoryList, accountId);

            for (TransactionDTO transactionDTO : transactionDTOList) {
                Transaction transaction = new Transaction();
                transaction.setAmount(transactionDTO.getAmount());
                transaction.setMessage(transactionDTO.getMessage());
                Optional<CategoryDTO> categoryDTO = findCategoryDTOByNameFromBackupFile(categoryDTOList, transactionDTO.getCategoryName());
                Optional<Category> category = findCategoryByNameFromList(categoryList, transactionDTO.getCategoryName());
                categoryDTO.ifPresent(dto -> category.ifPresent(c -> keywordService.setKeywordsFromCategoryDTO(dto, c, accountId)));
                category.ifPresent(transaction::setCategory);
                transaction.setAccount(overMoneyAccount);
                transaction.setDate(transactionDTO.getDate());
                transaction.setTelegramUserId(telegramId);

                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    private Optional<Category> findCategoryByNameFromList(List<Category> categoryList, String categoryName) {
        return categoryList.stream()
                .filter(category -> categoryName.equals(category.getName()))
                .findFirst();
    }

    private Optional<CategoryDTO> findCategoryDTOByNameFromBackupFile(List<CategoryDTO> categoryDTOList, String categoryDTOName) {
        return categoryDTOList.stream()
                .filter(categoryDTO -> categoryDTOName.equals(categoryDTO.getName()))
                .findFirst();
    }
}


