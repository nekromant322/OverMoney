package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> findTransactionsListByUserIdWithoutCategories(Long id) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        return transactionRepository.findAllWithoutCategoriesByAccountId(accID);
    }
}
