package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsMonthlyIncomeForCategoryDTO;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;


import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideMonthlyIncomeStatisticsForYear")
    public void findMonthlyIncomeStatisticsForYearByAccountIdReturnsCorrectList(List<AnalyticsMonthlyIncomeForCategoryDTO> inputList,
                                                                                List<AnalyticsMonthlyReportForYearDTO> requeredList) {

        when(transactionRepository.findMonthlyIncomeStatisticsByYearAndAccountId(any(), any()))
                .thenReturn(inputList);

        List<AnalyticsMonthlyReportForYearDTO> resultList = transactionService.findMonthlyIncomeStatisticsForYearByAccountId(123L, 123);

        Assertions.assertEquals(resultList, requeredList);
    }

    private static Stream<Arguments> provideMonthlyIncomeStatisticsForYear() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.generateTestAnalyticsMonthlyIncomeForCategoryWithNullFields(),
                        TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOWithNull()),
                Arguments.of(TestFieldsUtil.generateTestAnalyticsMonthlyIncomeForCategoryWithoutNullFields(),
                        TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOWithoutNull()),
                Arguments.of(TestFieldsUtil.generateTestAnalyticsMonthlyIncomeForCategoryWithMixedFields(),
                        TestFieldsUtil.generateTestListOfAnalyticsMonthlyReportForYearDTOMixed())
        );
    }

    @Test
    public void saveTransactionsListTest() {
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction1 = TestFieldsUtil.generateTestTransaction();
        Transaction transaction2 = TestFieldsUtil.generateTestTransaction();
        transactionList.add(transaction1);
        transactionList.add(transaction2);

        transactionService.saveTransactionsList(transactionList);

        verify(transactionRepository, times(1)).saveAll(any());
    }

    @Test
    public void deleteTransactionsByAccountIdTest() {
        Transaction transaction = new Transaction();
        OverMoneyAccount account = new OverMoneyAccount();
        account.setId(1L);
        transaction.setAccount(account);

        transactionRepository.deleteAllByAccountId(account.getId());

        verify(transactionRepository, times(1)).deleteAllByAccountId(account.getId());
    }

    @Test
    public void deleteTransactionByIdTest() {
        UUID id = UUID.randomUUID();

        transactionService.deleteTransactionById(id);

        verify(transactionRepository, times(1)).deleteById(id);
    }
}
