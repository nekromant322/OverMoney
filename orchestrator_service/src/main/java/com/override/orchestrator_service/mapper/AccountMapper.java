package com.override.orchestrator_service.mapper;

import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper {

    public List<Category> mapAccountToCategoryList(OverMoneyAccount account) {
        return new ArrayList<>(account.getCategories());
    }
}
