package com.override.orchestrator_service.service;

import com.override.dto.MonthSumTransactionByTypeCategoryDTO;
import com.override.dto.TransactionSummaryDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticV2Service {
    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionSummaryDTO countFinanceData(Long userID) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        List<MonthSumTransactionByTypeCategoryDTO> sumIncome = transactionRepository.findSumTransactionByTypeCategory(
                userID, currentYear, currentMonth, Type.INCOME);
        List<MonthSumTransactionByTypeCategoryDTO> sumExpense = transactionRepository.findSumTransactionByTypeCategory(
                userID, currentYear, currentMonth, Type.EXPENSE);
        return new TransactionSummaryDTO(sumIncome, sumExpense);
    }
}
