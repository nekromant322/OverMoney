package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticServiceTest {

    @InjectMocks
    private AnalyticService analyticService;

    @Mock
    private OverMoneyAccountService accountService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void getTotalCategorySumsForAnalyticsTest() throws InstanceNotFoundException {
        OverMoneyAccount acc = TestFieldsUtil.generateTestAccount();
        when(accountService.getAccountByUserId(any())).thenReturn(acc);
        AnalyticsDataDTO analyticsDataDTOTest1 = AnalyticsDataDTO.builder()
                                                                    .categoryId(123l)
                                                                    .categoryName("Тест 1")
                                                                    .sumOfTransactions(1000.0)
                                                                    .build();
        AnalyticsDataDTO analyticsDataDTOTest2 = AnalyticsDataDTO.builder()
                                                                    .categoryId(124l)
                                                                    .categoryName("Тест 2")
                                                                    .sumOfTransactions(2000.0)
                                                                    .build();
        List<AnalyticsDataDTO> analyticsDataListTest = List.of(analyticsDataDTOTest1, analyticsDataDTOTest2);
        when(categoryRepository.findTotalSumOfAllCategoriesByAccIdAndType(acc.getId(), Type.EXPENSE))
                .thenReturn(analyticsDataListTest);
        analyticService.getTotalCategorySumsForAnalytics(123L, Type.EXPENSE);
        Assertions.assertEquals(categoryRepository.findTotalSumOfAllCategoriesByAccIdAndType(acc.getId(), Type.EXPENSE).size(),
                                analyticsDataListTest.size());
    }
}
