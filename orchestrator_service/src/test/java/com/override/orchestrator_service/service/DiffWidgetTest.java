package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataMonthDiffDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiffWidgetTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private DiffWidgetService diffWidgetService;

    private final Long accountId = 2L;

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

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(accountId);

        assertNotNull(result);
        assertEquals(11, result.getCurrentMonthIncomeToPrevMonthDiff());
        assertEquals(25, result.getCurrentMonthExpenseToPrevMonthDiff());
        assertEquals(-9, result.getCurrentMonthIncomeToPrevYearDiff());
        assertEquals(-9, result.getCurrentMonthExpenseToPrevYearDiff());
        assertEquals(currentMonthName, result.getCurrentMonthName());
        assertEquals(previousMonthName, result.getPreviousMonthName());
    }

    @Test
    public void testNullgetMonthDiff() {
        LocalDate currentDate = LocalDate.of(2024, 9, 6);
        int currentMonth = currentDate.getMonthValue();
        int previousMonth = currentMonth - 1;
        int currentYear = currentDate.getYear();
        int previousYear = currentDate.getYear() - 1;

        List<SumTransactionsDataPerMonthForAccountDTO> currentMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 9, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 9, 0.0));
        List<SumTransactionsDataPerMonthForAccountDTO> previousMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 0.0));
        List<SumTransactionsDataPerMonthForAccountDTO> previousYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 9, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 9, 0.0));

        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, currentYear, currentMonth)).thenReturn(currentMonthData);
        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, currentYear, previousMonth)).thenReturn(previousMonthData);
        when(transactionRepository.findSumTransactionsPerSpecificMonthForAccount(
                accountId, previousYear, currentMonth)).thenReturn(previousYearSameMonthData);

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(accountId);

        assertNotNull(result);
        assertNull(result.getCurrentMonthIncomeToPrevMonthDiff());
        assertNull(result.getCurrentMonthExpenseToPrevMonthDiff());
        assertNull(result.getCurrentMonthIncomeToPrevYearDiff());
        assertNull(result.getCurrentMonthExpenseToPrevYearDiff());
    }
}
