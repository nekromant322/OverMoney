package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.mapper.AccountMapper;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private AccountMapper accountMapper;

    public List<CategoryDTO> findCategoriesListByUserId(Long id) throws InstanceNotFoundException {
        OverMoneyAccount account = accountService.getAccountByUserId(id);
        return categoryMapper.mapCategoriesListToJsonResponse(accountMapper.mapAccountToCategoryList(account));
    }
}