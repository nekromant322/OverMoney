package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.mapper.AnalyticsMapper;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticService {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private AnalyticsMapper analyticsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<AnalyticsDataDTO> getAnalyticsData(Long userId, Type type) throws InstanceNotFoundException {
        Long accId = accountService.getAccountByUserId(userId).getId();
        List<CategoryDTO> categories =
                categoryMapper.mapCategoriesListToJsonResponse(categoryRepository.findAllByTypeAndAccId(accId, type));
        List<AnalyticsDataDTO> analyticsData = new ArrayList<>();
        for (CategoryDTO categoryDTO : categories) {
            Long sumOfAmount = transactionService.getSumOfTransactionsByCategoryId(categoryDTO.getId());
            analyticsData.add(analyticsMapper.mapAnalyticsDataToJsonResponse(categoryDTO, sumOfAmount));
        }
        return analyticsData;
    }
}
