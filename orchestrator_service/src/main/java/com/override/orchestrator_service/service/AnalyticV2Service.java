package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataPerMonthDTO;
import com.override.dto.MonthSumTransactionByTypeCategoryDTO;
import com.override.dto.TransactionSummaryDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AnalyticV2Service {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;

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

    public AnalyticsDataPerMonthDTO countFinanceDataPerMonth(Long userId, Long selectedUserId, int year) throws InstanceNotFoundException {
        List<Long> userIds = new ArrayList<>();
        if (selectedUserId == null) {
            User currentUser = userService.getUserById(userId);
            OverMoneyAccount overMoneyAccount = currentUser.getAccount();
            Set<User> users = overMoneyAccount.getUsers();
            for (User user : users) {
                userIds.add(user.getId());
            }
        } else {
            userIds.add(selectedUserId);
        }

        final int months = 12;
        double[] monthsIncome = new double[months];
        double[] monthsExpense = new double[months];

        List<SumTransactionsDataPerMonthForAccountDTO> sumTransactions = transactionRepository.findSumTransactionsPerMonthForAccount(userIds, year);
        for (SumTransactionsDataPerMonthForAccountDTO data : sumTransactions) {
            int monthIndex = data.getMonth() - 1; // Преобразование месяца в индекс массива (0 - январь, 11 - декабрь)
            if (data.getType() == Type.INCOME) {
                monthsIncome[monthIndex] = data.getSum();
            } else if (data.getType() == Type.EXPENSE) {
                monthsExpense[monthIndex] = data.getSum();
            }
        }
        return new AnalyticsDataPerMonthDTO(monthsIncome, monthsExpense);
    }
}