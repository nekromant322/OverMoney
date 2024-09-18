package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataMonthDiffDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.constants.Type;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiffWidgetTest {
    @Mock
    private ExecutorService executorService = Mockito.mock(ExecutorService.class);
    @InjectMocks
    private DiffWidgetService diffWidgetService;

    private final Long overMoneyAccountId = 2L;
    private final LocalDate requestDate = LocalDate.of(2024, 9, 6);
    private final String baseMonthName = requestDate.minusMonths(1).getMonth()
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
    private final String previousMonthName = requestDate.minusMonths(2).getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

    @Test
    public void getMonthDiff() {
        List<SumTransactionsDataPerMonthForAccountDTO> baseMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 10000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 10000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 7, 9000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 7, 8000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 11000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 11000.0));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(baseMonthData),
                        ConcurrentUtils.constantFuture(prevMonthData),
                        ConcurrentUtils.constantFuture(prevYearSameMonthData));

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(overMoneyAccountId);

        assertNotNull(result);
        assertEquals(11, result.getBaseMonthIncomeToPrevMonthDiff());
        assertEquals(25, result.getBaseMonthExpenseToPrevMonthDiff());
        assertEquals(-9, result.getBaseMonthIncomeToPrevYearDiff());
        assertEquals(-9, result.getBaseMonthExpenseToPrevYearDiff());
        assertEquals(baseMonthName, result.getBaseMonthName());
        assertEquals(previousMonthName, result.getPreviousMonthName());
    }

    @Test
    public void getMonthDiffNoPreviousYear() {
        List<SumTransactionsDataPerMonthForAccountDTO> baseMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 10000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 10000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 7, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 7, 0.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 11000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 11000.0));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(baseMonthData),
                        ConcurrentUtils.constantFuture(prevMonthData),
                        ConcurrentUtils.constantFuture(prevYearSameMonthData));

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(overMoneyAccountId);

        assertNotNull(result);
        assertNull(result.getBaseMonthIncomeToPrevMonthDiff());
        assertNull(result.getBaseMonthExpenseToPrevMonthDiff());
        assertEquals(-9, result.getBaseMonthIncomeToPrevYearDiff());
        assertEquals(-9, result.getBaseMonthExpenseToPrevYearDiff());
        assertEquals(baseMonthName, result.getBaseMonthName());
        assertEquals(previousMonthName, result.getPreviousMonthName());
    }

    @Test
    public void getMonthDiffNoPreviousMonthTransactions() {
        List<SumTransactionsDataPerMonthForAccountDTO> baseMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 10000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 10000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 7, 9000.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 7, 8000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 0.0));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(baseMonthData),
                        ConcurrentUtils.constantFuture(prevMonthData),
                        ConcurrentUtils.constantFuture(prevYearSameMonthData));

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(overMoneyAccountId);

        assertNotNull(result);
        assertEquals(11, result.getBaseMonthIncomeToPrevMonthDiff());
        assertEquals(25, result.getBaseMonthExpenseToPrevMonthDiff());
        assertNull(result.getBaseMonthIncomeToPrevYearDiff());
        assertNull(result.getBaseMonthExpenseToPrevYearDiff());
        assertEquals(baseMonthName, result.getBaseMonthName());
        assertEquals(previousMonthName, result.getPreviousMonthName());
    }

    @Test
    public void getMonthDiffNoTransactions() {
        List<SumTransactionsDataPerMonthForAccountDTO> baseMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 10000.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 7, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 7, 0.0));

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData =
                Arrays.asList(new SumTransactionsDataPerMonthForAccountDTO(Type.INCOME, 8, 0.0),
                        new SumTransactionsDataPerMonthForAccountDTO(Type.EXPENSE, 8, 0.0));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(baseMonthData),
                        ConcurrentUtils.constantFuture(prevMonthData),
                        ConcurrentUtils.constantFuture(prevYearSameMonthData));

        AnalyticsDataMonthDiffDTO result = diffWidgetService.getMonthDiff(overMoneyAccountId);

        assertNotNull(result);
        assertNull(result.getBaseMonthIncomeToPrevMonthDiff());
        assertNull(result.getBaseMonthExpenseToPrevMonthDiff());
        assertNull(result.getBaseMonthIncomeToPrevYearDiff());
        assertNull(result.getBaseMonthExpenseToPrevYearDiff());
    }
}