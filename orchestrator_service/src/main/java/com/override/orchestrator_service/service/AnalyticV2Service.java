package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public AnalyticsMainDataPerYearsDTO countFinanceDataPerYear(Long id) throws InstanceNotFoundException {

        List<SummaryUsersDataPerYearDTO> data = new ArrayList<>();
        User currentUser = userService.getUserById(id);
        OverMoneyAccount overMoneyAccount = currentUser.getAccount();
        Set<User> users = overMoneyAccount.getUsers();

        List<Integer> years = transactionRepository.findAvailableYearsForAccountByAccountId(overMoneyAccount.getId());
        Collections.sort(years);
        for (int year : years) {
            SummaryUsersDataPerYearDTO summaryUsersDataPerYearDTO = new SummaryUsersDataPerYearDTO();
            List<UserIncomeExpenseCategoriesPerYearDTO> userIncomeExpenseCategoriesPerYearDTO = new ArrayList<>();
            summaryUsersDataPerYearDTO.setYear(year);
            for (User user : users) {
                UserIncomeExpenseCategoriesPerYearDTO userData = new UserIncomeExpenseCategoriesPerYearDTO();
                List<SumTransactionPerYearForAccountDTO> incomeTransactions =
                        transactionRepository.findSumTransactionsPerYearForAccount(user.getId(), year, Type.INCOME);
                List<SumTransactionPerYearForAccountDTO> expenseTransactions =
                        transactionRepository.findSumTransactionsPerYearForAccount(user.getId(), year, Type.EXPENSE);

                incomeTransactions.stream().map(t -> {
                   if (t.getSum() == null) {
                       t.setSum(0.0);
                   }
                   return t;
                }).collect(Collectors.toList());

                expenseTransactions.stream().map(t -> {
                    if (t.getSum() == null) {
                        t.setSum(0.0);
                    }
                    return t;
                }).collect(Collectors.toList());

                userData.setId(user.getId());
                userData.setCategoryIncome(incomeTransactions);
                userData.setCategoryExpense(expenseTransactions);
                userIncomeExpenseCategoriesPerYearDTO.add(userData);
            }
            summaryUsersDataPerYearDTO.setUsers(userIncomeExpenseCategoriesPerYearDTO);
            data.add(summaryUsersDataPerYearDTO);
        }

        return new AnalyticsMainDataPerYearsDTO(data);
    }
}