package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsMonthlyReportForYearDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;


import javax.management.InstanceNotFoundException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserService userService;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private OverMoneyAccountService accountService;

    @Test
    public void transactionRepositorySaveTransactionWhenCategoryAndTransactionFound() {
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transactionService.saveTransaction(transaction);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void setCategoryForAllUndefinedTransactionsWithSameKeywordsTest() {
        final Category category = TestFieldsUtil.generateTestCategory();
        final Transaction transaction = TestFieldsUtil.generateTestTransaction();
        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(transactionRepository.findAccountIdByTransactionId(transaction.getId())).thenReturn(account.getId());
        transactionService.setCategoryForAllUndefinedTransactionsWithSameKeywords(transaction.getId(), category.getId());
        verify(transactionRepository, times(1))
                .updateCategoryIdWhereCategoryIsNull(category.getId(), transaction.getMessage(), account.getId());
    }

    @Test
    public void findTransactionsListByUserIdWithoutCategoriesTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        account.setUsers(null);
        User user = TestFieldsUtil.generateTestUser();
        user.setAccount(account);
        Transaction transaction1 = TestFieldsUtil.generateTestTransaction();
        Transaction transaction2 = TestFieldsUtil.generateTestTransaction();
        transaction1.setCategory(null);
        transaction2.setCategory(null);
        when(transactionRepository.findAllWithoutCategoriesByAccountId(any()))
                .thenReturn(List.of(transaction1, transaction2));
        when(userService.getUserById(any())).thenReturn(user);
        List<Transaction> testListTransaction =
                transactionService.findTransactionsListByUserIdWithoutCategories(user.getId());
        Assertions.assertEquals(List.of(transaction1, transaction2), testListTransaction);
    }

    @Test
    public void findTransactionsLimitedByUserIdTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.
                generateTestAccount();
        account.setUsers(null);
        User user = TestFieldsUtil.generateTestUser();
        user.setAccount(account);
        Transaction transaction1 = TestFieldsUtil.generateTestTransaction();
        Transaction transaction2 = TestFieldsUtil.generateTestTransaction();
        TransactionDTO transactionDTO1 = TestFieldsUtil.generateTestTransactionDTO();
        TransactionDTO transactionDTO2 = TestFieldsUtil.generateTestTransactionDTO();
        Page<Transaction> page = new PageImpl<>(List.of(transaction1, transaction2));

        when(transactionRepository.findAllByAccountId(any(), any())).thenReturn(page);
        when(userService.getUserById(any())).thenReturn(user);
        when(transactionMapper.mapTransactionToDTO(transaction1)).thenReturn(transactionDTO1);
        when(transactionMapper.mapTransactionToDTO(transaction2)).thenReturn(transactionDTO2);

        List<TransactionDTO> testListTransaction =
                transactionService.findTransactionsByUserIdLimited(user.getId(), 50, 0);
        Assertions.assertEquals(List.of(transactionDTO1, transactionDTO2), testListTransaction);
    }

    @Test
    public void removeCategoryFromTransactionsWithSameMessageRemovesCategoryFromKeyword() {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();

        when(transactionRepository.findAccountIdByTransactionId(transaction.getId())).thenReturn(transaction.getAccount().getId());
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));


        transactionService.removeCategoryFromTransactionsWithSameMessage(transaction.getId());
        verify(transactionRepository, times(1))
                .removeCategoryIdFromTransactionsWithSameMessage(transaction.getMessage(), transaction.getAccount().getId());
    }

    @Test
    public void findAvailableYearsReturnsList() throws InstanceNotFoundException {
        OverMoneyAccount acc = TestFieldsUtil.generateTestAccount();
        List<Integer> listOfYears = List.of(1, 2, 3);

        when(transactionRepository.findAvailableYearsForAccountByAccountId(any()))
                .thenReturn(listOfYears);

        transactionService.findAvailableYears(123L);
        Assertions.assertEquals(transactionRepository.findAvailableYearsForAccountByAccountId(acc.getId()).size(),
                listOfYears.size());
    }

    @Test
    public void findMonthlyIncomeStatisticsForYearByAccountIdReturnsCorrectList() throws InstanceNotFoundException {
        List<Object[]> listOfObjects = new ArrayList<>();
        Map<Integer, Double> map = new HashMap<>();
        for (int counter = 1; counter <= 12; counter++) {
            map.put(counter, (double) counter);
            Object[] array = new Object[] {(double) counter, "cat1", counter};
            listOfObjects.add(array);
        }
        AnalyticsMonthlyReportForYearDTO dto1 = new AnalyticsMonthlyReportForYearDTO("cat1", map);

        List<AnalyticsMonthlyReportForYearDTO> list = List.of(dto1);

        when(transactionRepository.findMonthlyIncomeStatisticsByYearAndAccountId(any(), any()))
                .thenReturn(listOfObjects);

        List<AnalyticsMonthlyReportForYearDTO> resultList = transactionService.findMonthlyIncomeStatisticsForYearByAccountId(123L, 123);

        Assertions.assertEquals(resultList, list);
    }
}
