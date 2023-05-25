package com.override.orchestrator_service.service;

import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> findTransactionsListByUserId(Long id) throws InstanceNotFoundException {
        return new ArrayList<>(
                userService
                        .getUserById(id)
                        .getAccount()
                        .getTransactions()
        );
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
