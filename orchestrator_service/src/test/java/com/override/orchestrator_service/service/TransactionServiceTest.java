package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.SuggestionRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.repository.specification.TransactionSpecification;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Spy
    private TransactionSpecification transactionSpecification;

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
    @Mock
    private KeywordService keywordService;
    @Mock
    private SuggestionRepository suggestionRepository;

    @Test
    public void transactionRepositorySaveTransactionWhenCategoryAndTransactionFound() {
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setMessage("пиво 300");
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

    @ParameterizedTest
    @MethodSource("provideFilters")
    public void findTransactionsByUserIdLimitedAndFilteredTest(Long userId, TransactionFilter filter, List<TransactionDTO> expectedDTOs, Page<Transaction> page) throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        User user = TestFieldsUtil.generateTestUser();
        user.setAccount(account);

        when(userService.getUserById(userId)).thenReturn(user);
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        for (int i = 0; i < page.getContent().size(); i++) {
            when(transactionMapper.mapTransactionToDTO(page.getContent().get(i))).thenReturn(expectedDTOs.get(i));
        }
        List<TransactionDTO> result = transactionService.findTransactionsByUserIdLimitedAndFiltered(userId, filter);

        assertNotNull(result);
        assertEquals(expectedDTOs.size(), result.size());
        for (int i = 0; i < expectedDTOs.size(); i++) {
            assertEquals(expectedDTOs.get(i), result.get(i));
        }
        verify(transactionRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    private static Stream<Arguments> provideFilters() {
        Long userId = 1L;
        TransactionFilter filter1 = new TransactionFilter();
        filter1.setCategory(TestFieldsUtil.generateTestCategory());
        filter1.setAmount(new AmountRangeDTO(1000, 5000));
        filter1.setMessage("продукты");
        LocalDateTime beginDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();
        filter1.setDate(new DateRangeDTO(beginDate, endDate));
        filter1.setTelegramUserIdList(Arrays.asList(1L, 2L));
        filter1.setPageSize(50);
        filter1.setPageNumber(0);

        TransactionFilter filter2 = new TransactionFilter();
        filter2.setAmount(new AmountRangeDTO(100, 500));
        filter2.setMessage("продукты");
        filter2.setPageSize(50);
        filter2.setPageNumber(0);

        TransactionFilter filter3 = new TransactionFilter();
        filter3.setPageSize(50);
        filter3.setPageNumber(0);

        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .message("пиво")
                .amount(2000d)
                .date(LocalDateTime.now())
                .category(TestFieldsUtil.generateTestCategory())
                .account(TestFieldsUtil.generateTestAccount())
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .message("продукты")
                .amount(200d)
                .date(LocalDateTime.now())
                .category(TestFieldsUtil.generateTestCategory())
                .account(TestFieldsUtil.generateTestAccount())
                .build();
        Page<Transaction> page1 = new PageImpl<>(List.of(transaction1));
        Page<Transaction> page2 = new PageImpl<>(List.of(transaction2));
        Page<Transaction> page3 = new PageImpl<>(List.of(transaction1, transaction2));

        TransactionDTO transactionDTO1 = TransactionDTO.builder()
                .message("пиво")
                .amount(2000d)
                .date(LocalDateTime.now())
                .categoryName(TestFieldsUtil.generateTestCategory().getName())
                .build();

        TransactionDTO transactionDTO2 = TransactionDTO.builder()
                .message("продукты")
                .amount(200d)
                .date(LocalDateTime.now())
                .categoryName(TestFieldsUtil.generateTestCategory().getName())
                .build();

        List<TransactionDTO> expectedDTOList1 = List.of(transactionDTO1);
        List<TransactionDTO> expectedDTOList2 = List.of(transactionDTO2);
        List<TransactionDTO> expectedDTOList3 = List.of(transactionDTO1, transactionDTO2);

        return Stream.of(
                Arguments.of(userId, filter1, expectedDTOList1, page1),
                Arguments.of(userId, filter2, expectedDTOList2, page2),
                Arguments.of(userId, filter3, expectedDTOList3, page3)
        );
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

    @Test
    public void deleteTransactionsByIdsTest() {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        UUID id = transaction.getId();
        when(transactionRepository.findAllByIds(Collections.singletonList(id))).thenReturn(Collections.singletonList(transaction));
        transactionService.deleteTransactionByIds(Collections.singletonList(id));
        verify(suggestionRepository, times(1)).deleteByTransactionIds(Collections.singletonList(id));
        verify(transactionRepository, times(1)).findAllByIds(Collections.singletonList(id));
        verify(transactionRepository, times(1)).deleteByIds(Collections.singletonList(id));
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
                .accuracy(0.7F)
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();


        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);
        when(transactionRepository.findFirstTransactionByAccountIdOrderByDate(any())).thenReturn(transaction);

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
                .accuracy(0.7F)
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();

        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);
        when(transactionRepository.findFirstTransactionByAccountIdOrderByDate(any())).thenReturn(transaction);

        transactionService.editTransaction(transactionDTO);

        verify(keywordRepository, times(1)).deleteByKeywordId(any(KeywordId.class));
    }

    @Test
    public void editTransactionTestWhenChangesCategoryAndKeyword() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .message("тест")
                .amount(250d)
                .date(LocalDateTime.now())
                .categoryName("Тест")
                .accuracy(0.7F)
                .build();

        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        Optional<Keyword> keyword = Optional.ofNullable(TestFieldsUtil.generateTestKeyword());
        Category category = TestFieldsUtil.generateTestCategory();

        when(transactionRepository.findById(any())).thenReturn(Optional.ofNullable(transaction));
        when(keywordRepository.findByKeywordId(any())).thenReturn(keyword);
        when(categoryRepository.findCategoryByNameAndAccountId(any(), any())).thenReturn(category);
        when(transactionRepository.findFirstTransactionByAccountIdOrderByDate(any())).thenReturn(transaction);

        transactionService.editTransaction(transactionDTO);

        verify(keywordRepository, times(1)).delete(keyword.get());
    }

    @Test
    void patchTransaction_SuccessfulUpdate() throws InstanceNotFoundException {
        UUID transactionId = UUID.randomUUID();
        LocalDateTime date = TestFieldsUtil.generateTestTransaction().getDate();

        TransactionMessageDTO transactionMessageDTO = new TransactionMessageDTO();
        transactionMessageDTO.setMessage("фрукты 300");

        Transaction receivedTransactionFromReply = new Transaction();
        receivedTransactionFromReply.setMessage("гвозди");
        receivedTransactionFromReply.setAmount(300.0);
        receivedTransactionFromReply.setDate(date);

        TransactionResponseDTO expectedResponse = new TransactionResponseDTO();
        expectedResponse.setComment("гвозди");
        expectedResponse.setAmount("300");

        Transaction transactionFromRepo = TestFieldsUtil.generateTestTransaction();
        transactionFromRepo.setDate(date);

        when(transactionProcessingService.processTransaction(transactionMessageDTO)).thenReturn(receivedTransactionFromReply);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionFromRepo));
        when(transactionMapper.mapTransactionToTelegramResponse(any(Transaction.class))).thenReturn(new TransactionResponseDTO());

        TransactionResponseDTO response = transactionService.patchTransaction(transactionMessageDTO, transactionId);

        verify(transactionRepository).save(transactionFromRepo);
        assertNotNull(response);
        assertEquals(receivedTransactionFromReply.getMessage(), transactionFromRepo.getMessage());
        assertEquals(receivedTransactionFromReply.getAmount(), transactionFromRepo.getAmount());
    }
}
