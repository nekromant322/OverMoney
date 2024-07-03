package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.assertj.core.util.Arrays;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private TelegramBotFeign telegramBotFeign;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionProcessingService transactionProcessingService;

    @Test
    public void transactionRepositorySaveTransactionWhenCategoryAndTransactionFound() {
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transactionService.saveTransaction(transaction);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void saveAllTransactionsTest() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactionService.saveAllTransactions(transactions);
        verify(transactionRepository, times(1)).saveAll(transactions);
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
        assertEquals(List.of(transaction1, transaction2), testListTransaction);
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
        assertEquals(List.of(transactionDTO1, transactionDTO2), testListTransaction);
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
        assertEquals(transactionRepository.findAvailableYearsForAccountByAccountId(acc.getId()).size(),
                listOfYears.size());
    }

    @ParameterizedTest
    @MethodSource("provideMonthlyIncomeStatisticsForYear")
    public void findMonthlyIncomeStatisticsForYearByAccountIdReturnsCorrectList(List<AnalyticsMonthlyIncomeForCategoryDTO> inputList,
                                                                                List<AnalyticsMonthlyReportForYearDTO> requeredList) {

        when(transactionRepository.findMonthlyIncomeStatisticsByYearAndAccountId(any(), any()))
                .thenReturn(inputList);

        List<AnalyticsMonthlyReportForYearDTO> resultList = transactionService.findMonthlyIncomeStatisticsForYearByAccountId(123L, 123);

        assertEquals(resultList, requeredList);
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
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        UUID id = transaction.getId();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        transactionService.deleteTransactionById(id);
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(1)).deleteById(id);
    }

    @ParameterizedTest
    @MethodSource("provideAnnualAndMonthlyTotalStatisticsForYear")
    public void findAnnualAndMonthlyTotalStatisticsByAccountIdReturnsCorrectList(List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> inputList,
                                                                                 List<AnalyticsAnnualAndMonthlyReportDTO> outputList) {
        when(transactionRepository.findAnnualAndMonthlyTotalStatisticsByAccountId(any(), any()))
                .thenReturn(inputList);

        List<AnalyticsAnnualAndMonthlyReportDTO> resultList = transactionService
                .findAnnualAndMonthlyTotalStatisticsByAccountId(1L, 1999);

        assertEquals(resultList, outputList);
    }

    private static Stream<Arguments> provideAnnualAndMonthlyTotalStatisticsForYear() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.generateTestAnalyticsAnnualAndMonthlyExpenseForCategoryWithNullFields(),
                        TestFieldsUtil.generateTestListOfAnalyticsAnnualAndMonthlyReportDTOWithNull()),
                Arguments.of(TestFieldsUtil.generateTestAnalyticsAnnualAndMonthlyExpenseForCategoryWithoutNullFields(),
                        TestFieldsUtil.generateTestListOfAAnalyticsAnnualAndMonthlyReportDTOWithoutNull())
        );
    }

    @Test
    public void editTransactionTestWhenChangesKeyword() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .message("тест")
                .amount(188926.54D)
                .date(LocalDateTime.now())
                .categoryName("продукты")
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();


        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);

        transactionService.editTransaction(transactionDTO);

        verify(keywordRepository, times(1)).delete(keyword.get());
    }

    @Test
    public void editTransactionTestWhenChangesCategory() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .message("продукты")
                .amount(250d)
                .date(LocalDateTime.now())
                .categoryName("Тест")
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();

        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);

        transactionService.editTransaction(transactionDTO);

        verify(keywordRepository, times(1)).delete(keyword.get());
    }

    @Test
    public void editTransactionTestWhenChangesCategoryAndKeyword() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .message("тест")
                .amount(250d)
                .date(LocalDateTime.now())
                .categoryName("Тест")
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();

        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);

        transactionService.editTransaction(transactionDTO);

        verify(keywordRepository, times(2)).delete(keyword.get());
    }

    @Test
    void patchTransaction_SuccessfulUpdate() throws InstanceNotFoundException {
        UUID transactionId = UUID.randomUUID();

        TransactionMessageDTO transactionMessageDTO = new TransactionMessageDTO();
        TransactionMessageDTO transactionMessageDTO1 = new TransactionMessageDTO();
        TransactionMessageDTO transactionMessageDTO2 = new TransactionMessageDTO();
        TransactionMessageDTO transactionMessageDTO3 = new TransactionMessageDTO();

        transactionMessageDTO.setMessage("фрукты 100");
        transactionMessageDTO1.setMessage("100 фрукты");
        transactionMessageDTO2.setMessage("фрукты 100+200");
        transactionMessageDTO3.setMessage("100+200 фрукты");

        List<TransactionMessageDTO> transactionMessages = new ArrayList<>();
        transactionMessages.add(transactionMessageDTO);
        transactionMessages.add(transactionMessageDTO1);
        transactionMessages.add(transactionMessageDTO2);
        transactionMessages.add(transactionMessageDTO3);

        Transaction transactionFromRepo = TestFieldsUtil.generateTestTransaction();
        transactionFromRepo.setId(transactionId);
        Transaction receivedTransactionFromReply = new Transaction();

        for (TransactionMessageDTO transactionMessage : transactionMessages) {

            when(transactionProcessingService.processTransaction(transactionMessage)).thenReturn(receivedTransactionFromReply);
            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionFromRepo));
            when(transactionMapper.mapTransactionToTelegramResponse(any(Transaction.class))).thenReturn(new TransactionResponseDTO());

            TransactionResponseDTO response = transactionService.patchTransaction(transactionMessage, transactionId);

            verify(transactionRepository).save(transactionFromRepo);
            assertNotNull(response);
            assertEquals(receivedTransactionFromReply.getMessage(), transactionFromRepo.getMessage());
            assertEquals(receivedTransactionFromReply.getAmount(), transactionFromRepo.getAmount());
        }
    }
}
