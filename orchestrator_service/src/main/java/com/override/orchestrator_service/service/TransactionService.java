package com.override.orchestrator_service.service;

import com.override.dto.AnalyticsMonthlyIncomeForCategoryDTO;
import com.override.dto.AnalyticsMonthlyReportForYearDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<TransactionDTO> findAlltransactionDTOForAcountByUserId(Long telegramId) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getAccountByUserId(telegramId);
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
        return transactionRepository.findAllWithoutCategoriesByAccountId(accID);
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

    public List<TransactionDTO> findTransactionsByUserIdLimited(Long id, Integer pageSize, Integer pageNumber) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());

        List<TransactionDTO> transactionList = transactionRepository.findAllByAccountId(accID, pageable).getContent().stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
        return enrichTransactionsWithTgUsernames(transactionList);
    }

    private List<TransactionDTO> enrichTransactionsWithTgUsernames(List<TransactionDTO> transactionList) {
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

    public List<AnalyticsMonthlyReportForYearDTO> findMonthlyIncomeStatisticsForYearByAccountId(Long accountId, Integer year) {
        List<AnalyticsMonthlyIncomeForCategoryDTO> list = transactionRepository.findMonthlyIncomeStatisticsByYearAndAccountId(accountId, year);
        return mapObjectToAnalyticsMonthIncomeDTO(list);
    }

    private List<AnalyticsMonthlyReportForYearDTO> mapObjectToAnalyticsMonthIncomeDTO(List<AnalyticsMonthlyIncomeForCategoryDTO> objects) {
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
    public Transaction editTransaction(TransactionDTO transactionDTO) {
        Transaction transactionUpdate = getTransactionById(transactionDTO.getId());
        transactionUpdate.setAmount(transactionDTO.getAmount());

        KeywordId keywordId = new KeywordId();
        keywordId.setAccountId(transactionUpdate.getAccount().getId());
        keywordId.setName(transactionUpdate.getMessage());
        Keyword keyword = keywordRepository.findByKeywordId(keywordId);
        if (!transactionUpdate.getMessage().equals(transactionDTO.getMessage())) {
            if (keyword != null) {
                keywordRepository.delete(keyword);
            }
        }
        transactionUpdate.setMessage(transactionDTO.getMessage());

        if (!transactionUpdate.getCategory().getName().equals(transactionDTO.getCategoryName())) {
            Category category = categoryRepository.findCategoryByNameAndAccountId(transactionUpdate.getAccount().getId(),
                    transactionDTO.getCategoryName());
            transactionUpdate.setCategory(category);
            if (keyword != null) {
                keywordRepository.delete(keyword);
            }
        }
        return transactionRepository.save(transactionUpdate);
    }

    public void deleteTransactionById(UUID id) {
        transactionRepository.deleteById(id);
    }
}
