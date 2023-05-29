package com.override.orchestrator_service.service;

import com.override.orchestrator_service.config.RecentActivityProperties;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RecentActivityProperties recentActivityProperties;

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Long> findAccountsIDWithRecentTransactions() {
        LocalDateTime minimalDate = LocalDateTime.now().minusDays(recentActivityProperties.getActivityDays());
        return transactionRepository.findActiveAccounts(minimalDate);
    }

    public List<Transaction> findTransactionsListByUserIdWithoutCategories(Long id) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        return transactionRepository.findAllWithoutCategoriesByAccountId(accID);
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(TransactionNotFoundException::new);
    }

    public void setTransactionCategory(UUID transactionId, UUID categoryId) {
        Transaction transaction = getTransactionById(transactionId);
        Category category = categoryService.getCategoryById(categoryId);
        if (Objects.nonNull(transaction) && Objects.nonNull(category)) {
            transaction.setCategory(category);
            transactionRepository.save(transaction);
        }
    }
}
