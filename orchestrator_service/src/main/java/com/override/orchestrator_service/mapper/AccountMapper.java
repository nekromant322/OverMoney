package com.override.orchestrator_service.mapper;

import com.override.dto.AccountDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TransactionMapper transactionMapper;

    public List<Category> mapAccountListToCategoryList(List<OverMoneyAccount> accounts) {
        List<Category> categories = new ArrayList<>();
        accounts.forEach(account -> categories.addAll(account.getCategories()));
        return categories;
    }

    public List<Category> mapAccountToCategoryList(OverMoneyAccount account) {
        return new ArrayList<>(account.getCategories());
    }

    public AccountDataDTO mapAccountToJson(OverMoneyAccount account) {
        return AccountDataDTO.builder()
                .chatId(account.getChatId())
                .categories(getAccountCategoryDTOList(account))
                .transactions(getTransactionsList(account))
                .build();
    }

    private List<CategoryDTO> getAccountCategoryDTOList(OverMoneyAccount account) {
        return categoryMapper.mapCategoriesListToJsonResponse(new ArrayList<>(account.getCategories()));
    }

    private List<TransactionResponseDTO> getTransactionsList(OverMoneyAccount account) {
        return transactionMapper.mapTransactionListToJsonList(new ArrayList<>(account.getTransactions()));
    }
}
