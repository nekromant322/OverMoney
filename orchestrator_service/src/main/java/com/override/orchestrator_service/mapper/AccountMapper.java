package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper {

    public List<Category> mapAccountListToCategoryList(List<OverMoneyAccount> accounts) {
        List<Category> categories = new ArrayList<>();
        accounts.forEach(account -> categories.addAll(account.getCategories()));
        return categories;
    }

    public List<Category> mapAccountToCategoryList(OverMoneyAccount account) {
        return new ArrayList<>(account.getCategories());
    }
}
