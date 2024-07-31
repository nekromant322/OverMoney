package com.override.orchestrator_service.service;

import com.override.dto.*;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.repository.specification.TransactionSpecification;
import com.override.orchestrator_service.util.NumericalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OverMoneyAccountService overMoneyAccountService;
    @Autowired
    private TelegramBotFeign telegramBotFeign;
    @Autowired
    private TransactionProcessingService transactionProcessingService;

    public int getTransactionsCount() {
        return transactionRepository.getTransactionsCount();
    }

    public List<TransactionDTO> findAlltransactionDTOForAcountByChatId(Long telegramId) {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getOverMoneyAccountByChatId(telegramId);
        List<Transaction> transactionList = transactionRepository.findAllByAccountId(overMoneyAccount.getId());
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        transactionList.forEach(transaction -> transactionDTOS.add(transactionMapper.mapTransactionToDTO(transaction)));

        return transactionDTOS;
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> findTransactionsListByUserIdWithoutCategories(Long id) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        return transactionRepository.findAllWithoutCategoriesByAccountId(accID)
                .stream()
                .peek(tr -> tr.setAmount(NumericalUtils.roundAmount(tr.getAmount())))
                .collect(Collectors.toList());
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(TransactionNotFoundException::new);
    }

    public void updateCategory(Long categoryToMergeId, Long categoryToChangeId) {
        transactionRepository.updateCategoryId(categoryToMergeId, categoryToChangeId);
    }

    @Transactional
    public void setCategoryForAllUndefinedTransactionsWithSameKeywords(UUID transactionId, Long categoryId) {
        Long accId = transactionRepository.findAccountIdByTransactionId(transactionId);
        String transactionMessage = getTransactionById(transactionId).getMessage();
        transactionRepository.updateCategoryIdWhereCategoryIsNull(categoryId, transactionMessage, accId);
    }

    public List<TransactionDTO> findTransactionsByUserIdLimited(Long id, Integer pageSize, Integer pageNumber)
            throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());

        List<TransactionDTO> transactionList =
                transactionRepository.findAllByAccountId(accID, pageable).getContent().stream()
                        .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                        .collect(Collectors.toList());
        return enrichTransactionsWithTgUsernames(transactionList);
    }

    public List<TransactionDTO> findTransactionsByUserIdLimitedAndFiltered(Long id, TransactionFilter filter)
            throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize(), Sort.by("date").descending());

        Specification<Transaction> spec = TransactionSpecification.createSpecification(accID, filter);

        List<TransactionDTO> transactionList = transactionRepository.findAll(spec, pageable).getContent().stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
        return enrichTransactionsWithTgUsernames(transactionList);
    }

    public List<TransactionDTO> enrichTransactionsWithTgUsernames(List<TransactionDTO> transactionList) {
        Map<Long, User> userMap = userService.getUsersByIds(transactionList.stream()
                        .map(TransactionDTO::getTelegramUserId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        transactionList.forEach(transactionDTO -> {
            User user = userMap.get(transactionDTO.getTelegramUserId());
            if (user != null) {
                if (user.getUsername() != null) {
                    transactionDTO.setTelegramUserName(user.getUsername());
                } else {
                    transactionDTO.setTelegramUserName(user.getFirstName());
                }
            }
        });

        return transactionList;
    }

    @Transactional
    public void removeCategoryFromTransactionsWithSameMessage(UUID transactionId) {
        Long accountId = transactionRepository.findAccountIdByTransactionId(transactionId);
        String transactionMessage = getTransactionById(transactionId).getMessage();
        transactionRepository.removeCategoryIdFromTransactionsWithSameMessage(transactionMessage, accountId);
    }

    public Transaction enrichTransactionWithSuggestedCategory(TransactionDTO transactionDTO) {
        Transaction transaction = getTransactionById(transactionDTO.getId());
        transaction.setSuggestedCategoryId(transactionDTO.getSuggestedCategoryId());
        return transaction;
    }

    public List<Integer> findAvailableYears(Long accountId) {
        return transactionRepository.findAvailableYearsForAccountByAccountId(accountId);
    }

    public List<AnalyticsMonthlyReportForYearDTO> findMonthlyIncomeStatisticsForYearByAccountId(Long accountId,
                                                                                                Integer year) {
        List<AnalyticsMonthlyIncomeForCategoryDTO> list =
                transactionRepository.findMonthlyIncomeStatisticsByYearAndAccountId(accountId, year);
        return mapObjectToAnalyticsMonthIncomeDTO(list);
    }

    private List<AnalyticsMonthlyReportForYearDTO> mapObjectToAnalyticsMonthIncomeDTO(
            List<AnalyticsMonthlyIncomeForCategoryDTO> objects) {
        Set<String> setOfCategoryNames = new HashSet<>();
        List<AnalyticsMonthlyReportForYearDTO> result = new ArrayList<>();
        objects.forEach(object -> {
            setOfCategoryNames.add(object.getCategoryName());
        });
        setOfCategoryNames.forEach(categoryName -> {
            Map<Integer, Double> monthlyAnalytics = new HashMap<>();
            objects.forEach(object -> {
                if (Objects.equals(categoryName, object.getCategoryName())) {
                    monthlyAnalytics.put(object.getMonth(), object.getAmount());
                }
            });
            for (Integer monthCounter = 1; monthCounter <= 12; monthCounter++) {
                if (!monthlyAnalytics.containsKey(monthCounter)) {
                    monthlyAnalytics.put(monthCounter, 0d);
                }
            }
            result.add(new AnalyticsMonthlyReportForYearDTO(categoryName, monthlyAnalytics));
        });
        return result;
    }

    @Transactional
    public void editTransaction(TransactionDTO transactionDTO) {
        Transaction transactionUpdate = getTransactionById(transactionDTO.getId());
        transactionUpdate.setAmount(transactionDTO.getAmount());
        Optional<Keyword> keyword = getKeywordByTransaction(transactionUpdate);
        if (!transactionUpdate.getMessage().equals(transactionDTO.getMessage())) {
            keyword.ifPresent(k -> keywordRepository.delete(k));
        }
        transactionUpdate.setMessage(transactionDTO.getMessage());

        Category category = categoryRepository.findCategoryByNameAndAccountId(transactionUpdate.getAccount().getId(),
                transactionDTO.getCategoryName());
        if (transactionUpdate.getCategory() != null) {
            if (!transactionUpdate.getCategory().getName().equals(transactionDTO.getCategoryName())) {
                transactionUpdate.setCategory(category);
                keyword.ifPresent(k -> keywordRepository.delete(k));
            }
        } else if (!transactionDTO.getCategoryName().equals("Нераспознанное")) {
            if (transactionDTO.getMessage() != null) {
                KeywordId newKeywordId = new KeywordId();
                newKeywordId.setAccountId(transactionUpdate.getAccount().getId());
                newKeywordId.setName(transactionDTO.getMessage());
                Keyword newKeyword = new Keyword();
                newKeyword.setKeywordId(newKeywordId);
                newKeyword.setCategory(category);
                keywordRepository.save(newKeyword);
            }
            transactionUpdate.setCategory(category);
        }
        transactionRepository.save(transactionUpdate);
    }

    public void saveAllTransactions(List<Transaction> transactionList) {
        transactionRepository.saveAll(transactionList);
    }

    @Transactional
    public void deleteTransactionById(UUID id) {
        Optional<Transaction> transactionToDelete = transactionRepository.findById(id);
        if (transactionToDelete.isPresent()) {
            getKeywordByTransaction(transactionToDelete.get()).ifPresent(k -> keywordRepository.delete(k));
            transactionRepository.deleteById(id);
            telegramBotFeign.deleteTelegramMessageById(id);
        }
    }

    public Optional<Keyword> getKeywordByTransaction(Transaction transaction) {
        KeywordId keywordId = new KeywordId();
        keywordId.setAccountId(transaction.getAccount().getId());
        keywordId.setName(transaction.getMessage());
        return keywordRepository.findByKeywordId(keywordId);
    }

    public List<AnalyticsAnnualAndMonthlyReportDTO> findAnnualAndMonthlyTotalStatisticsByAccountId(Long accountId,
                                                                                                   Integer year) {
        List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> list =
                transactionRepository.findAnnualAndMonthlyTotalStatisticsByAccountId(accountId, year);
        return mapObjectToAnalyticsAnnualAndMonthlyReportDTO(list);
    }

    private List<AnalyticsAnnualAndMonthlyReportDTO> mapObjectToAnalyticsAnnualAndMonthlyReportDTO(
            List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> objects) {
        Set<String> setOfCategoryNames = new HashSet<>();
        List<AnalyticsAnnualAndMonthlyReportDTO> result = new ArrayList<>();

        objects.forEach(object -> setOfCategoryNames.add(object.getCategoryName()));

        setOfCategoryNames.forEach(categoryName -> {
            final int[] categoryId = new int[1];
            Map<Integer, Double> monthlyAnalytics = new HashMap<>();
            Map<Integer, Double> shareOfMonthlyExpenses = new HashMap<>();
            objects.forEach(object -> {
                if (Objects.equals(categoryName, object.getCategoryName())) {
                    categoryId[0] = object.getCategoryId();
                    if (monthlyAnalytics.containsKey(object.getMonth()) &&
                            shareOfMonthlyExpenses.containsKey(object.getMonth())) {
                        monthlyAnalytics.replace(object.getMonth(),
                                NumericalUtils.roundAmount(
                                        monthlyAnalytics.get(object.getMonth()) + object.getAmount()));
                        shareOfMonthlyExpenses.replace(object.getMonth(),
                                NumericalUtils.roundAmount(
                                        shareOfMonthlyExpenses.get(object.getMonth()) + object.getAmount()));
                    } else {
                        monthlyAnalytics.put(object.getMonth(), NumericalUtils.roundAmount(object.getAmount()));
                        shareOfMonthlyExpenses.put(object.getMonth(), NumericalUtils.roundAmount(object.getAmount()));
                    }
                }
            });
            for (Integer monthCounter = 1; monthCounter <= 12; monthCounter++) {
                if (!monthlyAnalytics.containsKey(monthCounter)) {
                    monthlyAnalytics.put(monthCounter, 0d);
                    shareOfMonthlyExpenses.put(monthCounter, 0d);
                }
            }
            result.add(new AnalyticsAnnualAndMonthlyReportDTO(categoryName, categoryId[0], monthlyAnalytics,
                    shareOfMonthlyExpenses));
        });
        return replaceShareOfMonthlyExpenses(objects, result);
    }

    private List<AnalyticsAnnualAndMonthlyReportDTO> replaceShareOfMonthlyExpenses(
            List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> objects,
            List<AnalyticsAnnualAndMonthlyReportDTO> result) {
        List<Integer> months = getListOfMonthNumbers(objects);
        Map<Integer, Double> total = getTotalExpenseByYearAndMonths(objects, result);

        months.forEach(month -> result.forEach(dto -> {
            Double amount = dto.getMonthlyAnalytics().get(month);
            if (amount != 0) {
                Double totalAmount = total.get(month);
                dto.getShareOfMonthlyExpenses().replace(month, 1 - ((totalAmount - amount) / totalAmount));
            }
        }));
        return result;
    }

    private Map<Integer, Double> getTotalExpenseByYearAndMonths(
            List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> objects,
            List<AnalyticsAnnualAndMonthlyReportDTO> result) {
        Map<Integer, Double> totalMonthlyAnalytics = new HashMap<>();
        List<Integer> monthNumberList = getListOfMonthNumbers(objects);

        monthNumberList.forEach(month -> {
            List<Double> miniTotalMonthlyAnalytics = new ArrayList<>();
            result.forEach(dto -> miniTotalMonthlyAnalytics.add(dto.getMonthlyAnalytics().get(month)));
            totalMonthlyAnalytics.put(month, miniTotalMonthlyAnalytics.stream().reduce(0d, Double::sum));
        });
        return totalMonthlyAnalytics;
    }

    private List<Integer> getListOfMonthNumbers(List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> objects) {
        Set<Integer> months = objects.stream()
                .map(AnalyticsAnnualAndMonthlyExpenseForCategoryDTO::getMonth)
                .collect(Collectors.toSet());
        for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
            months.add(monthCounter);
        }
        return months.stream().sorted().collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDTO patchTransaction(TransactionMessageDTO transactionMessage,
                                                   UUID id) throws InstanceNotFoundException {

        Transaction receivedTransactionFromReply = transactionProcessingService.processTransaction(transactionMessage);

        Transaction transactionToUpdate = transactionRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("Транзакция не найдена"));

        if (receivedTransactionFromReply.getDate().isEqual(transactionToUpdate.getDate())) {
            Stream.of(transactionToUpdate)
                    .filter(t -> receivedTransactionFromReply.getMessage() != null)
                    .filter(t -> !receivedTransactionFromReply.getMessage().equals(t.getMessage()))
                    .forEach(transaction -> transaction.setMessage(receivedTransactionFromReply.getMessage()));

            Stream.of(transactionToUpdate)
                    .filter(t -> receivedTransactionFromReply.getAmount() != null)
                    .filter(t -> !receivedTransactionFromReply.getAmount().equals(t.getAmount()))
                    .forEach(transaction -> transaction.setAmount(receivedTransactionFromReply.getAmount()));

            transactionRepository.save(transactionToUpdate);
            return transactionMapper.mapTransactionToTelegramResponse(transactionToUpdate);
        } else {
            throw new DateTimeException("Даты транзакций не совпадают");
        }
    }

    public List<TransactionDTO> getTransactionsListByPeriodAndCategory(Integer year, Integer month, long categoryId) {
        return transactionRepository.findTransactionsBetweenDatesAndCategory(year, month, categoryId).stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }

    public int getTransactionsCountLastDays(int numberDays) {
        LocalDateTime numberDaysAgo = LocalDateTime.now().minusDays(numberDays);
        return transactionRepository.findCountTransactionsLastDays(numberDaysAgo);
    }
}
