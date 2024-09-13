package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataMonthDiffDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.repository.TransactionRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class DiffWidgetService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    @Qualifier("diffWidgetExecutor")
    private ExecutorService executorService;

    @SneakyThrows
    public AnalyticsDataMonthDiffDTO getMonthDiff(Long overMoneyAccountId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int previousMonth = currentMonth - 1;
        int currentYear = currentDate.getYear();
        int previousYear = currentDate.getYear() - 1;
        String currentMonthName = currentDate.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        String previousMonthName = currentDate.minusMonths(1).getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

        Future<List<SumTransactionsDataPerMonthForAccountDTO>> completableFutureCurrMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, currentMonth);
        Future<List<SumTransactionsDataPerMonthForAccountDTO>> completableFuturePrevMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, previousMonth);
        Future<List<SumTransactionsDataPerMonthForAccountDTO>> completableFuturePrevYrSameMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, previousYear, currentMonth);

        List<SumTransactionsDataPerMonthForAccountDTO> currentMonthData = completableFutureCurrMth.get();
        double currentMonthIncome = calculateMonthSumByType(currentMonthData, Type.INCOME);
        double currentMonthExpense = calculateMonthSumByType(currentMonthData, Type.EXPENSE);

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData = completableFuturePrevMth.get();
        double prevMonthIncome = calculateMonthSumByType(prevMonthData, Type.INCOME);
        double prevMonthExpense = calculateMonthSumByType(prevMonthData, Type.EXPENSE);

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData = completableFuturePrevYrSameMth.get();
        double prevYearSameMonthIncome = calculateMonthSumByType(prevYearSameMonthData, Type.INCOME);
        double prevYearSameMonthExpense = calculateMonthSumByType(prevYearSameMonthData, Type.EXPENSE);

        return new AnalyticsDataMonthDiffDTO(calculatePercentageDiff(currentMonthIncome, prevMonthIncome),
                calculatePercentageDiff(currentMonthExpense, prevMonthExpense),
                calculatePercentageDiff(currentMonthIncome, prevYearSameMonthIncome),
                calculatePercentageDiff(currentMonthExpense, prevYearSameMonthExpense),
                currentMonthName, previousMonthName);
    }

    public Future<List<SumTransactionsDataPerMonthForAccountDTO>> getTransactionsForSpecifiedMonthAsync(
            Long overMoneyAccountId, int currentYear, int currentMonth) {
        return executorService.submit(() -> transactionRepository.
                findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId,
                        currentYear, currentMonth));
    }

    private double calculateMonthSumByType(List<SumTransactionsDataPerMonthForAccountDTO> data, Type type) {
        return data.stream().filter(t -> t.getType().equals(type)).
                mapToDouble(SumTransactionsDataPerMonthForAccountDTO::getSum).sum();
    }

    protected Integer calculatePercentageDiff(double currentData, double previousData) {
        return previousData == 0.0 ? null : BigDecimal
                .valueOf(((currentData - previousData) / previousData) * 100).intValue();
    }
}