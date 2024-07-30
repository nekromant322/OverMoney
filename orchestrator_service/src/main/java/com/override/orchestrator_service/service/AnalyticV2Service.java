package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataPerMonthDTO;
import com.override.dto.AnalyticsMainDataPerYearsDTO;
import com.override.dto.MonthSumTransactionByTypeCategoryDTO;
import com.override.dto.SumTransactionPerYearForAccountDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.SummaryUsersDataPerYearDTO;
import com.override.dto.TransactionSummaryDTO;
import com.override.dto.UserIncomeExpenseCategoriesPerYearDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.exception.InvalidDataException;
import com.override.orchestrator_service.exception.UserNotFoundException;
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
        User currentUser = userService.getUserById(id);
        OverMoneyAccount overMoneyAccount = currentUser.getAccount();
        if (overMoneyAccount == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        try {
            Set<User> users = overMoneyAccount.getUsers();
            List<Integer> years = getSortedYearsForAccount(overMoneyAccount.getId());
            List<SummaryUsersDataPerYearDTO> data = years.stream()
                    .map(year -> createSummaryDataForYear(users, year))
                    .collect(Collectors.toList());

            return new AnalyticsMainDataPerYearsDTO(data);
        } catch (Exception e) {
            throw new InvalidDataException("Возникла ошибка при получении данных: " + e.getMessage());
        }
    }

    public List<Integer> getSortedYearsForAccount(Long accountId) {
        List<Integer> years = transactionRepository.findAvailableYearsForAccountByAccountId(accountId);
        Collections.sort(years);
        return years;
    }

    public SummaryUsersDataPerYearDTO createSummaryDataForYear(Set<User> users, int year) {
        SummaryUsersDataPerYearDTO summaryUsersDataPerYearDTO = new SummaryUsersDataPerYearDTO();
        summaryUsersDataPerYearDTO.setYear(year);

        List<UserIncomeExpenseCategoriesPerYearDTO> userIncomeExpenseCategoriesPerYearDTO = users.stream()
                .map(user -> createUserIncomeExpenseDataForYear(user, year))
                .collect(Collectors.toList());

        summaryUsersDataPerYearDTO.setUsers(userIncomeExpenseCategoriesPerYearDTO);
        return summaryUsersDataPerYearDTO;
    }

    public UserIncomeExpenseCategoriesPerYearDTO createUserIncomeExpenseDataForYear(User user, int year) {
        UserIncomeExpenseCategoriesPerYearDTO userData = new UserIncomeExpenseCategoriesPerYearDTO();
        userData.setId(user.getId());

        List<SumTransactionPerYearForAccountDTO> incomeTransactions = getTransactionsForYear(user.getId(), year, Type.INCOME);
        List<SumTransactionPerYearForAccountDTO> expenseTransactions = getTransactionsForYear(user.getId(), year, Type.EXPENSE);

        userData.setCategoryIncome(incomeTransactions);
        userData.setCategoryExpense(expenseTransactions);
        return userData;
    }

    public List<SumTransactionPerYearForAccountDTO> getTransactionsForYear(Long userId, int year, Type type) {
        List<SumTransactionPerYearForAccountDTO> transactions = transactionRepository.findSumTransactionsPerYearForAccount(userId, year, type);
        return transactions.stream()
                .map(t -> {
                    if (t.getSum() == null) {
                        t.setSum(0.0);
                    }
                    return t;
                }).collect(Collectors.toList());
    }
}