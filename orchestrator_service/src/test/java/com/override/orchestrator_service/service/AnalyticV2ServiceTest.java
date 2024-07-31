package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsMainDataPerYearsDTO;
import com.override.dto.SumTransactionPerYearForAccountDTO;
import com.override.dto.SummaryUsersDataPerYearDTO;
import com.override.dto.UserIncomeExpenseCategoriesPerYearDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        overMoneyAccount = new OverMoneyAccount();
        overMoneyAccount.setId(1L);
        user.setAccount(overMoneyAccount);
        overMoneyAccount.setUsers(Set.of(user));
    }

    @Test
    public void testCountFinanceDataPerYear() throws InstanceNotFoundException {
        when(userService.getUserById(1L)).thenReturn(user);
        when(transactionRepository.findAvailableYearsForAccountByAccountId(1L)).thenReturn(Arrays.asList(2022, 2023));

        List<SumTransactionPerYearForAccountDTO> incomeTransactions2022 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(1L,"Зарплата", 50000.0));
        List<SumTransactionPerYearForAccountDTO> expenseTransactions2022 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(2L, "Аренда", 20000.0));

        List<SumTransactionPerYearForAccountDTO> incomeTransactions2023 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(3L, "Бонус", 30000.0));
        List<SumTransactionPerYearForAccountDTO> expenseTransactions2023 = Collections.singletonList(new SumTransactionPerYearForAccountDTO(4L, "Продукты", 10000.0));

        when(transactionRepository.findSumTransactionsPerYearForAccount(1L, 2022, Type.INCOME)).thenReturn(incomeTransactions2022);
        when(transactionRepository.findSumTransactionsPerYearForAccount(1L, 2022, Type.EXPENSE)).thenReturn(expenseTransactions2022);
        when(transactionRepository.findSumTransactionsPerYearForAccount(1L, 2023, Type.INCOME)).thenReturn(incomeTransactions2023);
        when(transactionRepository.findSumTransactionsPerYearForAccount(1L, 2023, Type.EXPENSE)).thenReturn(expenseTransactions2023);

        AnalyticsMainDataPerYearsDTO result = analyticV2Service.countFinanceDataPerYear(1L);

        assertNotNull(result);
        assertEquals(2, result.getData().size());

        SummaryUsersDataPerYearDTO year2022 = result.getData().get(0);
        assertEquals(2022, year2022.getYear());
        assertEquals(1, year2022.getUsers().size());

        UserIncomeExpenseCategoriesPerYearDTO userData2022 = year2022.getUsers().get(0);
        assertEquals(1L, userData2022.getId());
        assertEquals(1, userData2022.getCategoryIncome().size());
        assertEquals("Зарплата", userData2022.getCategoryIncome().get(0).getName());
        assertEquals(50000.0, userData2022.getCategoryIncome().get(0).getSum());
        assertEquals(1, userData2022.getCategoryExpense().size());
        assertEquals("Аренда", userData2022.getCategoryExpense().get(0).getName());
        assertEquals(20000.0, userData2022.getCategoryExpense().get(0).getSum());

        SummaryUsersDataPerYearDTO year2023 = result.getData().get(1);
        assertEquals(2023, year2023.getYear());
        assertEquals(1, year2023.getUsers().size());

        UserIncomeExpenseCategoriesPerYearDTO userData2023 = year2023.getUsers().get(0);
        assertEquals(1L, userData2023.getId());
        assertEquals(1, userData2023.getCategoryIncome().size());
        assertEquals("Бонус", userData2023.getCategoryIncome().get(0).getName());
        assertEquals(30000.0, userData2023.getCategoryIncome().get(0).getSum());
        assertEquals(1, userData2023.getCategoryExpense().size());
        assertEquals("Продукты", userData2023.getCategoryExpense().get(0).getName());
        assertEquals(10000.0, userData2023.getCategoryExpense().get(0).getSum());
    }
}
