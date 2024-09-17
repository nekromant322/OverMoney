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
        int baseMonth = currentDate.getMonthValue() - 1;
        int previousMonth = baseMonth - 1;
        int currentYear = currentDate.getYear();
        int previousYear = currentDate.getYear() - 1;
        String baseMonthName = currentDate.minusMonths(1)
                .getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        String previousMonthName = currentDate.minusMonths(2).getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

        Future<List<SumTransactionsDataPerMonthForAccountDTO>> futureBaseMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, baseMonth);
        Future<List<SumTransactionsDataPerMonthForAccountDTO>> futurePrevMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, currentYear, previousMonth);
        Future<List<SumTransactionsDataPerMonthForAccountDTO>> futurePrevYrSameMth =
                getTransactionsForSpecifiedMonthAsync(overMoneyAccountId, previousYear, baseMonth);

        List<SumTransactionsDataPerMonthForAccountDTO> baseMonthData = futureBaseMth.get();
        double baseMonthIncome = calculateMonthSumByType(baseMonthData, Type.INCOME);
        double baseMonthExpense = calculateMonthSumByType(baseMonthData, Type.EXPENSE);

        List<SumTransactionsDataPerMonthForAccountDTO> prevMonthData = futurePrevMth.get();
        double prevMonthIncome = calculateMonthSumByType(prevMonthData, Type.INCOME);
        double prevMonthExpense = calculateMonthSumByType(prevMonthData, Type.EXPENSE);

        List<SumTransactionsDataPerMonthForAccountDTO> prevYearSameMonthData = futurePrevYrSameMth.get();
        double prevYearSameMonthIncome = calculateMonthSumByType(prevYearSameMonthData, Type.INCOME);
        double prevYearSameMonthExpense = calculateMonthSumByType(prevYearSameMonthData, Type.EXPENSE);

        return new AnalyticsDataMonthDiffDTO(calculatePercentageDiff(baseMonthIncome, prevMonthIncome),
                calculatePercentageDiff(baseMonthExpense, prevMonthExpense),
                calculatePercentageDiff(baseMonthIncome, prevYearSameMonthIncome),
                calculatePercentageDiff(baseMonthExpense, prevYearSameMonthExpense),
                baseMonthName, previousMonthName);
    }

    public Future<List<SumTransactionsDataPerMonthForAccountDTO>> getTransactionsForSpecifiedMonthAsync(
            Long overMoneyAccountId, int currentYear, int requiredMonth) {
        return executorService.submit(() -> transactionRepository.
                findSumTransactionsPerSpecificMonthForAccount(overMoneyAccountId,
                        currentYear, requiredMonth));
    }

    private double calculateMonthSumByType(List<SumTransactionsDataPerMonthForAccountDTO> data, Type type) {
        return data.stream().filter(t -> t.getType().equals(type)).
                mapToDouble(SumTransactionsDataPerMonthForAccountDTO::getSum).sum();
    }

    protected Integer calculatePercentageDiff(double baseData, double previousData) {
        return previousData == 0.0 ? null : BigDecimal
                .valueOf(((baseData - previousData) / previousData) * 100).intValue();
    }
}