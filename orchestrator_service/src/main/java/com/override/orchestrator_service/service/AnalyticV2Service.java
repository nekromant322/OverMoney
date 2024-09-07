package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.exception.InvalidDataException;
import com.override.orchestrator_service.exception.UserNotFoundException;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
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

    public AnalyticsDataPerMonthDTO countFinanceDataPerMonth(Long userId, Long selectedUserId, int year)
            throws InstanceNotFoundException {
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

        List<SumTransactionsDataPerMonthForAccountDTO> sumTransactions =
                transactionRepository.findSumTransactionsPerMonthForAccount(userIds, year);
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
                    .map(year -> createSummaryDataForYear(overMoneyAccount.getId(), users, year))
                    .collect(Collectors.toList());

            return new AnalyticsMainDataPerYearsDTO(data);
        } catch (Exception e) {
            throw new InvalidDataException("Возникла ошибка при получении данных: " + e.getMessage());
        }
    }

    public List<Integer> getSortedYearsForAccount(Long overMoneyAccountId) {
        List<Integer> years = transactionRepository.findAvailableYearsForAccountByAccountId(overMoneyAccountId);
        Collections.sort(years);
        return years;
    }

    public SummaryUsersDataPerYearDTO createSummaryDataForYear(Long overMoneyAccountId, Set<User> users, int year) {
        SummaryUsersDataPerYearDTO summaryUsersDataPerYearDTO = new SummaryUsersDataPerYearDTO();
        summaryUsersDataPerYearDTO.setYear(year);

        List<UserIncomeExpenseCategoriesPerYearDTO> userIncomeExpenseCategoriesPerYearDTO = users.stream()
                .map(user -> createUserIncomeExpenseDataForYear(overMoneyAccountId, user, year))
                .collect(Collectors.toList());

        summaryUsersDataPerYearDTO.setUsers(userIncomeExpenseCategoriesPerYearDTO);
        return summaryUsersDataPerYearDTO;
    }

    public UserIncomeExpenseCategoriesPerYearDTO createUserIncomeExpenseDataForYear(
            Long overMoneyAccountId, User user, int year) {
        UserIncomeExpenseCategoriesPerYearDTO userData = new UserIncomeExpenseCategoriesPerYearDTO();
        userData.setId(user.getId());

        List<SumTransactionPerYearForAccountDTO> incomeTransactions =
                getTransactionsForYear(overMoneyAccountId, user.getId(), year, Type.INCOME);
        List<SumTransactionPerYearForAccountDTO> expenseTransactions =
                getTransactionsForYear(overMoneyAccountId, user.getId(), year, Type.EXPENSE);

        userData.setCategoryIncome(incomeTransactions);
        userData.setCategoryExpense(expenseTransactions);
        return userData;
    }

    public List<SumTransactionPerYearForAccountDTO> getTransactionsForYear(
            Long overMoneyAccountId, Long userId, int year, Type type) {
        List<SumTransactionPerYearForAccountDTO> transactions =
                transactionRepository.findSumTransactionsPerYearForAccount(overMoneyAccountId, userId, year, type);
        return transactions.stream()
                .map(t -> {
                    if (t.getSum() == null) {
                        t.setSum(0.0);
                    }
                    return t;
                }).collect(Collectors.toList());
    }

    public AnalyticsDataMonthDiffDTO getMonthDiff (Long overMoneyAccountId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int previousMonth = currentMonth - 1;
        int currentYear = currentDate.getYear();
        int previousYear = currentDate.getYear() - 1;
        String currentMonthName = currentDate.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        String previousMonthName = currentDate.minusMonths(1).getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

        List<SumTransactionsDataPerMonthForAccountDTO> currentMonthData = transactionRepository
                .findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId, currentYear, currentMonth);
        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData = transactionRepository
                .findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId, currentYear, previousMonth);
        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData = transactionRepository
                .findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId, previousYear, currentMonth);

        double currentMonthIncome = calculateMonthSumByType(currentMonthData, Type.INCOME);
        double currentMonthExpense = calculateMonthSumByType(currentMonthData, Type.EXPENSE);
        double prevMonthIncome = calculateMonthSumByType(prevMonthData, Type.INCOME);
        double prevMonthExpense = calculateMonthSumByType(prevMonthData, Type.EXPENSE);
        double prevYearSameMonthIncome = calculateMonthSumByType(prevYearSameMonthData, Type.INCOME);
        double prevYearSameMonthExpense = calculateMonthSumByType(prevYearSameMonthData, Type.EXPENSE);

        return new AnalyticsDataMonthDiffDTO(calculatePercentageDiff(currentMonthIncome, prevMonthIncome),
                calculatePercentageDiff(currentMonthExpense, prevMonthExpense),
                calculatePercentageDiff(currentMonthIncome, prevYearSameMonthIncome),
                calculatePercentageDiff(currentMonthExpense, prevYearSameMonthExpense),
                currentMonthName, previousMonthName);
    }

    private double calculateMonthSumByType(List<SumTransactionsDataPerMonthForAccountDTO> data, Type type) {
        return data.stream().filter(t -> t.getType().equals(type)).
                mapToDouble(SumTransactionsDataPerMonthForAccountDTO::getSum).sum();
    }

    private Integer calculatePercentageDiff(double currentData, double previousData) {
        return previousData == 0.0 ? null : BigDecimal
                .valueOf(((currentData - previousData) / previousData) * 100).intValue();
    }
}