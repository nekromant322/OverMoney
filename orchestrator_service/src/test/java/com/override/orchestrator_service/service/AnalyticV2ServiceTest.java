package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticV2ServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AnalyticV2Service analyticV2Service;

    private User user;
    private OverMoneyAccount overMoneyAccount;
    private final Long userId = 1L;
    private final Long accountId = 2L;

    private final int year2022 = 2022;
    private final int year2023 = 2023;

    private final Long salaryId = 3L;
    private final Long rentId = 4L;
    private final Long bonusId = 5L;
    private final Long groceriesId = 6L;

    private final String salaryCategory = "Зарплата";
    private final String rentCategory = "Аренда";
    private final String bonusCategory = "Бонус";
    private final String groceriesCategory = "Продукты";

    private final Double salaryAmount = 50000.0;
    private final Double rentAmount = 20000.0;
    private final Double bonusAmount = 30000.0;
    private final Double groceriesAmount = 10000.0;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(userId);
        overMoneyAccount = new OverMoneyAccount();
        overMoneyAccount.setId(accountId);
        user.setAccount(overMoneyAccount);
        overMoneyAccount.setUsers(Set.of(user));
    }

    @Test
    public void testCountFinanceDataPerYear() throws InstanceNotFoundException {
        when(userService.getUserById(userId)).thenReturn(user);
        when(transactionRepository.findAvailableYearsForAccountByAccountId(accountId)).thenReturn(Arrays.asList(year2022, year2023));

        List<SumTransactionPerYearForAccountDTO> incomeTransactions2022 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(salaryId, salaryCategory, salaryAmount));
        List<SumTransactionPerYearForAccountDTO> expenseTransactions2022 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(rentId, rentCategory, rentAmount));
        List<SumTransactionPerYearForAccountDTO> incomeTransactions2023 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(bonusId, bonusCategory, bonusAmount));
        List<SumTransactionPerYearForAccountDTO> expenseTransactions2023 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(groceriesId, groceriesCategory, groceriesAmount));

        when(transactionRepository.findSumTransactionsPerYearForAccount(accountId, userId, year2022, Type.INCOME)).thenReturn(incomeTransactions2022);
        when(transactionRepository.findSumTransactionsPerYearForAccount(accountId, userId, year2022, Type.EXPENSE)).thenReturn(expenseTransactions2022);
        when(transactionRepository.findSumTransactionsPerYearForAccount(accountId, userId, year2023, Type.INCOME)).thenReturn(incomeTransactions2023);
        when(transactionRepository.findSumTransactionsPerYearForAccount(accountId, userId, year2023, Type.EXPENSE)).thenReturn(expenseTransactions2023);

        AnalyticsMainDataPerYearsDTO result = analyticV2Service.countFinanceDataPerYear(userId);

        assertNotNull(result);
        assertEquals(2, result.getData().size());

        SummaryUsersDataPerYearDTO year2022Data = result.getData().get(0);
        assertEquals(year2022, year2022Data.getYear());
        assertEquals(1, year2022Data.getUsers().size());

        UserIncomeExpenseCategoriesPerYearDTO userData2022 = year2022Data.getUsers().get(0);
        assertEquals(userId, userData2022.getId());
        assertEquals(1, userData2022.getCategoryIncome().size());
        assertEquals(salaryCategory, userData2022.getCategoryIncome().get(0).getName());
        assertEquals(salaryAmount, userData2022.getCategoryIncome().get(0).getSum());
        assertEquals(1, userData2022.getCategoryExpense().size());
        assertEquals(rentCategory, userData2022.getCategoryExpense().get(0).getName());
        assertEquals(rentAmount, userData2022.getCategoryExpense().get(0).getSum());

        SummaryUsersDataPerYearDTO year2023Data = result.getData().get(1);
        assertEquals(year2023, year2023Data.getYear());
        assertEquals(1, year2023Data.getUsers().size());

        UserIncomeExpenseCategoriesPerYearDTO userData2023 = year2023Data.getUsers().get(0);
        assertEquals(userId, userData2023.getId());
        assertEquals(1, userData2023.getCategoryIncome().size());
        assertEquals(bonusCategory, userData2023.getCategoryIncome().get(0).getName());
        assertEquals(bonusAmount, userData2023.getCategoryIncome().get(0).getSum());
        assertEquals(1, userData2023.getCategoryExpense().size());
        assertEquals(groceriesCategory, userData2023.getCategoryExpense().get(0).getName());
        assertEquals(groceriesAmount, userData2023.getCategoryExpense().get(0).getSum());

        // Верификация psql вызовов методов репозитория
        verify(transactionRepository).findSumTransactionsPerYearForAccount(accountId, userId, year2022, Type.INCOME);
        verify(transactionRepository).findSumTransactionsPerYearForAccount(accountId, userId, year2022, Type.EXPENSE);
        verify(transactionRepository).findSumTransactionsPerYearForAccount(accountId, userId, year2023, Type.INCOME);
        verify(transactionRepository).findSumTransactionsPerYearForAccount(accountId, userId, year2023, Type.EXPENSE);
    }

    @Test
    public void testGetMonthDiff() {
        LocalDate currentDate = LocalDate.of(2024, 9, 6);
        int currentMonth = currentDate.getMonthValue();
        int previousMonth = currentMonth - 1;
        int currentYear = currentDate.getYear();
        int previousYear = currentDate.getYear() - 1;
        String currentMonthName = currentDate.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        String previousMonthName = currentDate.minusMonths(1).getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

        List<SumTransactionsDataPerMonthForAccountDTO> currentMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 9, 10000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 9, 10000.0));
        List<SumTransactionsDataPerMonthForAccountDTO> previousMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 9000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 8000.0));
        List<SumTransactionsDataPerMonthForAccountDTO> previousYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 9, 11000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 9, 11000.0));

        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, currentYear, currentMonth)).thenReturn(currentMonthData);
        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, currentYear, previousMonth)).thenReturn(previousMonthData);
        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, previousYear, currentMonth)).thenReturn(previousYearSameMonthData);

        AnalyticsDataMonthDiffDTO result = analyticV2Service.getMonthDiff(accountId);

        assertNotNull(result);
        assertEquals(11, result.getCurrentMonthIncomeToPrevMonthDiff());
        assertEquals(25, result.getCurrentMonthExpenseToPrevMonthDiff());
        assertEquals(-9, result.getCurrentMonthIncomeToPrevYearDiff());
        assertEquals(-9, result.getCurrentMonthExpenseToPrevYearDiff());
        assertEquals(currentMonthName, result.getCurrentMonthName());
        assertEquals(previousMonthName, result.getPreviousMonthName());
    }
}
