package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.dto.constants.Period;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticService {

    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<AnalyticsDataDTO> getTotalCategorySumsForAnalytics(Long userId, Type type)
            throws InstanceNotFoundException {
        Long accId = accountService.getAccountByUserId(userId).getId();
        List<AnalyticsDataDTO> list = categoryRepository.findMediumAmountOfAllCategoriesByAccIdAndType(accId, type);
        return list.stream()
                .filter(dto -> dto.getMediumAmountOfTransactions() != null)
                .peek(dto -> dto.setMediumAmountOfTransactions(
                        Math.round(dto.getMediumAmountOfTransactions().doubleValue())))
                .collect(Collectors.toList());
    }

    public List<Integer> findAvailableYears(Long telegramId) throws InstanceNotFoundException {
        Long accountId = accountService.getAccountByUserId(telegramId).getId();
        return transactionService.findAvailableYears(accountId);
    }

    public List<AnalyticsMonthlyReportForYearDTO> findMonthlyIncomeStatisticsForYearByAccountId(Long telegramId,
                                                                                                Integer year)
            throws InstanceNotFoundException {
        Long accountId = accountService.getAccountByUserId(telegramId).getId();
        return transactionService.findMonthlyIncomeStatisticsForYearByAccountId(accountId, year);
    }

    public List<AnalyticsDataMonthDTO> getTotalIncomeOutcomePerMonth(Long telegramId, int year)
            throws InstanceNotFoundException {
        Long accountId = userService.getUserById(telegramId).getAccount().getId();
        String sql = "SELECT to_char(t.date, 'Month') AS month, " +
                "   SUM(CASE WHEN c.type = 0 THEN t.amount ELSE 0 END) AS totalIncome," +
                "   SUM(CASE WHEN c.type = 1 THEN t.amount ELSE 0 END) AS totalExpense" +
                " FROM transactions t" +
                "   JOIN categories c ON t.category_id = c.id" +
                " WHERE t.account_id = :accountId" +
                "   AND EXTRACT(YEAR FROM t.date) = :year" +
                " GROUP BY month " +
                " ORDER BY month ";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("accountId", accountId);
        query.setParameter("year", year);

        return mapObjectListToDTO(query.getResultList());
    }

    private List<AnalyticsDataMonthDTO> mapObjectListToDTO(List<Object[]> objectList) {
        List<AnalyticsDataMonthDTO> analyticsDataMonthDTOS;

        analyticsDataMonthDTOS = objectList.stream()
                .map(item -> new AnalyticsDataMonthDTO((String) item[0], (Double) item[1], (Double) item[2]))
                .collect(Collectors.toList());

        return analyticsDataMonthDTOS;
    }

    public List<AnalyticsAnnualAndMonthlyReportDTO> findAnnualAndMonthlyTotalStatisticsByAccountId(Long telegramId,
                                                                                                   Integer year)
            throws InstanceNotFoundException {
        Long accountId = accountService.getAccountByUserId(telegramId).getId();
        return transactionService.findAnnualAndMonthlyTotalStatisticsByAccountId(accountId, year);
    }

    public List<SumTransactionPerCategoryPerPeriodDTO> getUserCategoriesWithSumOfTransactionsPerPeriod(
            Long id, Period period
    ) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();

        List<SumTransactionPerCategoryPerPeriodDTO> sumsList = new ArrayList<>();

        switch (period) {
            case YEAR:
                sumsList = categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear()
                );
                break;
            case MONTH:
                sumsList = categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonthValue()
                );
                break;
            case DAY:
                sumsList = categoryRepository.getCategoriesWithSumOfTransactionsByPeriodForAccount(
                        accID,
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonthValue(),
                        LocalDateTime.now().getDayOfMonth()
                );
        }

        List<Category> categoriesList = categoryRepository.findAllByUserId(accID);

        for (Category category : categoriesList) {
            if (sumsList.stream().noneMatch(x -> x.getName().equals(category.getName()))) {
                sumsList.add(SumTransactionPerCategoryPerPeriodDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .sum(0.0)
                        .type(category.getType())
                        .build());
            }
        }
        return sumsList;
    }
}
