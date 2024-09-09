package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataMonthDiffDTO;
import com.override.dto.SumTransactionsDataPerMonthForAccountDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class DiffWidgetService {
    @Autowired
    private TransactionRepository transactionRepository;

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

        CompletableFuture<List<SumTransactionsDataPerMonthForAccountDTO>> completableFutureCurrMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, currentMonth);
        CompletableFuture<List<SumTransactionsDataPerMonthForAccountDTO>> completableFuturePrevMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, previousMonth);
        CompletableFuture<List<SumTransactionsDataPerMonthForAccountDTO>> completableFuturePrevYrSameMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, previousYear, currentMonth);

        List<SumTransactionsDataPerMonthForAccountDTO> currentMonthData = completableFutureCurrMth.join();
        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData = completableFuturePrevMth.join();
        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData = completableFuturePrevYrSameMth.join();

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

    @Async("widgetAsyncExecutor")
    public CompletableFuture<List<SumTransactionsDataPerMonthForAccountDTO>> getTransactionsForSpecifiedMonthAsync(
            Long overMoneyAccountId, int currentYear, int currentMonth) {
        return CompletableFuture.supplyAsync(() -> transactionRepository.
                findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId, currentYear, currentMonth));
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
