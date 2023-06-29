package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.AnalyticsMonthlyReportForYearDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class AnalyticService {

    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionService transactionService;

    public List<AnalyticsDataDTO> getTotalCategorySumsForAnalytics(Long userId, Type type) throws InstanceNotFoundException {
        Long accId = accountService.getAccountByUserId(userId).getId();
        return categoryRepository.findTotalSumOfAllCategoriesByAccIdAndType(accId, type);
    }

    public List<Integer> findAvailableYears(Long telegramId) throws InstanceNotFoundException {
        Long accountId = accountService.getAccountByUserId(telegramId).getId();
        return transactionService.findAvailableYears(accountId);
    }

    public List<AnalyticsMonthlyReportForYearDTO> findMonthlyIncomeStatisticsForYearByAccountId(Long telegramId, Integer year) throws InstanceNotFoundException {
        Long accountId = accountService.getAccountByUserId(telegramId).getId();
        return transactionService.findMonthlyIncomeStatisticsForYearByAccountId(accountId, year);
    }
}
