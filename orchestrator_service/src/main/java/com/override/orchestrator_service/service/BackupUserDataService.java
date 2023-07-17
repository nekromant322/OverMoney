package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.KeywordId;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserService userService;
    @Autowired
    private KeywordService keywordService;

    public BackupUserDataDTO createBackupUserData(Long telegramId) throws InstanceNotFoundException {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByUserId(telegramId);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByUserId(telegramId);

        return BackupUserDataDTO.builder()
                .categoryDTOList(categoryDTOList)
                .transactionDTOList(transactionDTOList).build();
    }

    @Transactional
    public void writingDataFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<Transaction> transactionList = createTransactionsFromBackupFile(backupUserDataDTO, telegramId);
        if (transactionList.size() == backupUserDataDTO.getTransactionDTOList().size()) {
            transactionService.deleteTransactionsByAccountId(userService.getAccountByTelegramId(telegramId).getId());
            transactionService.overwriteTransactionsFromBackupFile(transactionList);
        }
    }

    private List<Transaction> createTransactionsFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<TransactionDTO> transactionDTOList = backupUserDataDTO.getTransactionDTOList();
        List<CategoryDTO> categoryDTOList = backupUserDataDTO.getCategoryDTOList();
        List<Transaction> transactionList = new ArrayList<>();
        Long accountId = userService.getAccountByTelegramId(telegramId).getId();
        transactionDTOList.replaceAll(t -> t.getCategoryName() != null ? t : replaceAllNull(t));


        for (TransactionDTO transactionDTO : transactionDTOList) {
            Transaction transaction = new Transaction();
            transaction.setId(transactionDTO.getId());
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setMessage(transactionDTO.getMessage());
            Optional<Category> category = categoryService.getCategoryByNameAndAccountId(accountId, transactionDTO.getCategoryName());
            Optional<CategoryDTO> categoryDTO = findCategoryDTOByNameFromBackupFile(categoryDTOList, transactionDTO.getCategoryName());

            if (category.isPresent()) {
                setKeywords(categoryDTO, category.get(), accountId);
                transaction.setCategory(category.get());
            } else {
                if (categoryDTO.isPresent()) {
                    Category categoryNew = categoryMapper.mapCategoryDTOToCategory(categoryDTO.get(),
                            userService.getAccountByTelegramId(telegramId));
                    categoryService.saveCategory(categoryNew);
                    setKeywords(categoryDTO, categoryNew, accountId);
                    transaction.setCategory(categoryNew);
                }
            }
            transaction.setAccount(userService.getAccountByTelegramId(telegramId));
            transaction.setDate(transactionDTO.getDate());
            transaction.setSuggestedCategoryId(transactionDTO.getSuggestedCategoryId());
            transaction.setTelegramUserId(telegramId);

            transactionList.add(transaction);
        }
        return transactionList;
    }

    private Optional<CategoryDTO> findCategoryDTOByNameFromBackupFile(List<CategoryDTO> categoryDTOList, String categoryDTOName) {
        return categoryDTOList.stream()
                .filter(categoryDTO -> categoryDTOName.equals(categoryDTO.getName()))
                .findFirst();
    }

    private void setKeywords(Optional<CategoryDTO> categoryDTO, Category category, Long accountId) {
        if (categoryDTO.isPresent()) {
            List<KeywordIdDTO> keywordIdDTOList = categoryDTO.get().getKeywords();

            for (KeywordIdDTO keywordIdDTO : keywordIdDTOList) {
                KeywordId keywordId = new KeywordId();
                keywordId.setAccountId(accountId);
                keywordId.setName(keywordIdDTO.getName());
                Keyword keyword = new Keyword();
                keyword.setKeywordId(keywordId);
                keyword.setCategory(category);
                keywordService.saveKeyword(keyword);
            }
        }
    }

    private TransactionDTO replaceAllNull(TransactionDTO transactionDTO) {
        TransactionDTO updateTransactionDTO = new TransactionDTO();
        updateTransactionDTO.setId(transactionDTO.getId());
        updateTransactionDTO.setCategoryName("Нераспознанное");
        updateTransactionDTO.setMessage(transactionDTO.getMessage());
        updateTransactionDTO.setAmount(transactionDTO.getAmount());
        updateTransactionDTO.setDate(transactionDTO.getDate());
        updateTransactionDTO.setSuggestedCategoryId(transactionDTO.getSuggestedCategoryId());
        updateTransactionDTO.setTelegramUserId(transactionDTO.getTelegramUserId());
        updateTransactionDTO.setTelegramUserName(transactionDTO.getTelegramUserName());

        return updateTransactionDTO;
    }
}


