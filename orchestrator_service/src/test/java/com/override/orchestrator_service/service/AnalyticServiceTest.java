package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.AnalyticsMonthlyReportForYearDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticServiceTest {

    @InjectMocks
    private AnalyticService analyticService;

    @Mock
    private OverMoneyAccountService accountService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Test
    public void getTotalCategorySumsForAnalyticsTest() throws InstanceNotFoundException {
        OverMoneyAccount acc = TestFieldsUtil.generateTestAccount();
        User testUser = TestFieldsUtil.generateTestUser();
        testUser.setAccount(acc);

        AnalyticsDataDTO analyticsDataDTOTest1 = AnalyticsDataDTO.builder()
                .categoryId(123l)
                .categoryName("Тест 1")
                .mediumAmountOfTransactions(1000.0)
                .build();
        AnalyticsDataDTO analyticsDataDTOTest2 = AnalyticsDataDTO.builder()
                .categoryId(124l)
                .categoryName("Тест 2")
                .mediumAmountOfTransactions(2000.0)
                .build();
        List<AnalyticsDataDTO> analyticsDataListTest = List.of(analyticsDataDTOTest1, analyticsDataDTOTest2);

        when(categoryRepository.findMediumAmountOfAllCategoriesByAccIdAndType(acc.getId(), Type.EXPENSE))
                .thenReturn(analyticsDataListTest);

        when(accountService.getAccountByUserId(any())).thenReturn(acc);

        analyticService.getTotalCategorySumsForAnalytics(123L, Type.EXPENSE);
        Assertions.assertEquals(categoryRepository.findMediumAmountOfAllCategoriesByAccIdAndType(acc.getId(), Type.EXPENSE).size(),
                analyticsDataListTest.size());
    }

    @Test
    public void findAvailableYearsReturnsList() throws InstanceNotFoundException {
        OverMoneyAccount acc = TestFieldsUtil.generateTestAccount();
        User testUser = TestFieldsUtil.generateTestUser();
        testUser.setAccount(acc);

        List<Integer> listOfYears = List.of(2020, 2021, 2023);

        when(transactionRepository.findAvailableYearsForAccountByAccountId(any()))
                .thenReturn(listOfYears);
        when(transactionService.findAvailableYears(any()))
                .thenReturn(listOfYears);
        when(accountService.getAccountByUserId(any())).thenReturn(acc);
        analyticService.findAvailableYears(123L);
        Assertions.assertEquals(transactionRepository.findAvailableYearsForAccountByAccountId(acc.getId()).size(),
                listOfYears.size());
    }

    @ParameterizedTest
    @MethodSource("provideMonthlyIncomeStatisticsForYearByAccountId")
    public void findMonthlyIncomeStatisticsForYearByAccountIdReturnsCorrectList(List<AnalyticsMonthlyReportForYearDTO> requeredList) throws InstanceNotFoundException {
        OverMoneyAccount acc = TestFieldsUtil.generateTestAccount();
        User testUser = TestFieldsUtil.generateTestUser();
        testUser.setAccount(acc);

        when(transactionService.findMonthlyIncomeStatisticsForYearByAccountId(any(), any()))
                .thenReturn(requeredList);
        when(accountService.getAccountByUserId(any()))
                .thenReturn(acc);

        List<AnalyticsMonthlyReportForYearDTO> resultList = analyticService.findMonthlyIncomeStatisticsForYearByAccountId(123L, 123);


        Assertions.assertEquals(resultList, requeredList);
    }

    private static Stream<Arguments> provideMonthlyIncomeStatisticsForYearByAccountId() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOWithNull()),
                Arguments.of(TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOWithoutNull()),
                Arguments.of(TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOMixed())
        );
    }
}
