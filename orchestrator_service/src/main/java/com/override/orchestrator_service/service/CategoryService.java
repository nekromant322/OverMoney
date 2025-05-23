package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.MergeCategoryDTO;
import com.override.dto.SumTransactionPerCategoryPerPeriodDTO;
import com.override.dto.constants.Period;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.config.DefaultCategoryProperties;
import com.override.orchestrator_service.exception.CategoryNameIsNotUniqueException;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.mapper.AccountMapper;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.KeywordId;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private DefaultCategoryProperties defaultCategoryProperties;
    @Autowired
    private UserService userService;

    public List<CategoryDTO> findCategoriesListByUserId(Long id) throws InstanceNotFoundException {
        OverMoneyAccount account = categoryRepository.findOverMoneyAccountByTelegramId(id);
        return categoryMapper.mapCategoriesListToJsonResponse(new ArrayList<>(categoryRepository
                .findCategoriesByOverMoneyAccount(account)));
    }

    public List<CategoryDTO> findCategoriesListByChatId(Long id) {
        OverMoneyAccount account = accountService.getOverMoneyAccountByChatId(id);
        return categoryMapper.mapCategoriesListToJsonResponse(accountMapper.mapAccountToCategoryList(account));
    }

    public Set<Category> getCategoriesByUserId(Long id) {
        return categoryRepository.findAllByAccount_Id(id);
    }

    public List<CategoryDTO> findCategoriesListByType(Long id, Type type) throws InstanceNotFoundException {
        Long accId = accountService.getAccountByUserId(id).getId();
        return categoryMapper.mapCategoriesListToJsonResponse(categoryRepository.findAllByTypeAndAccId(accId, type));
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
    }

    public CategoryDTO findCategoryById(Long categoryId) {
        return categoryMapper.mapCategoryToJsonResponse(
                categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new));
    }

    public void setDefaultCategoryForAccount(Long id) throws InstanceNotFoundException {
        OverMoneyAccount account = accountService.getAccountByUserId(id);
        defaultCategoryProperties.getCategories()
                .forEach(category -> categoryRepository.save(new Category(
                        category.getName(),
                        category.getType(),
                        account
                )));
    }

    public void saveCategoryForAcc(Long id, CategoryDTO categoryDTO) throws InstanceNotFoundException {
        validateCategoryName(categoryDTO.getName());

        OverMoneyAccount account = accountService.getAccountByUserId(id);

        try {
            categoryRepository.save(categoryMapper.mapCategoryDTOToCategory(categoryDTO, account));
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameIsNotUniqueException("Наименование категории \""
                    + categoryDTO.getName() + "\" не уникально!");
        }
    }

    public void updateCategoryForAcc(Long id, CategoryDTO categoryDTO) throws InstanceNotFoundException {
        validateCategoryName(categoryDTO.getName());

        OverMoneyAccount account = accountService.getAccountByUserId(id);
        Category updatedCategory = categoryMapper.mapCategoryDTOToCategory(categoryDTO, account);
        updatedCategory.setId(categoryDTO.getId());
        categoryRepository.save(updatedCategory);
    }

    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }
        if (name.length() > 40) {
            throw new IllegalArgumentException("Название категории не может быть длиннее 40 символов");
        }
    }

    public void saveAllCategories(Set<Category> categories) {
        categoryRepository.saveAll(categories);
    }

    @Transactional
    public void mergeCategory(MergeCategoryDTO categoryIDs) {
        Long categoryToMergeId = categoryIDs.getCategoryToMergeId();
        Long categoryToChangeId = categoryIDs.getCategoryToChangeId();
        keywordRepository.updateCategoryId(categoryToMergeId, categoryToChangeId);
        transactionRepository.updateCategoryId(categoryToMergeId, categoryToChangeId);
        categoryRepository.deleteById(categoryToMergeId);
    }

    @Transactional
    public void deleteKeyword(KeywordId keywordId) {
        keywordRepository.deleteByKeywordId(keywordId);
    }

    public Optional<CategoryDTO> findCategoryDTOByNameFromList(List<CategoryDTO> categoryDTOList,
                                                               String categoryDTOName) {
        return categoryDTOList.stream()
                .filter(categoryDTO -> categoryDTOName.equals(categoryDTO.getName()))
                .findFirst();
    }

    public List<SumTransactionPerCategoryPerPeriodDTO> getUserCategoriesWithSumOfTransactionsPerPeriod(
            Long id, Period period
    ) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        switch (period) {
            case YEAR:
                return categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear()
                );
            case MONTH:
                return categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonthValue()
                );
            case DAY:
                return categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonthValue(),
                        LocalDateTime.now().getDayOfMonth()
                );
        }
        return List.of();
    }
}