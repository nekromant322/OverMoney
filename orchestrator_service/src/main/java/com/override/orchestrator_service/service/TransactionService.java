package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionMapper transactionMapper;

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

    public void setTransactionCategory(UUID transactionId, Long categoryId) {
        Transaction transaction = getTransactionById(transactionId);
        Category category = categoryService.getCategoryById(categoryId);
        if (Objects.nonNull(transaction) && Objects.nonNull(category)) {
            transaction.setCategory(category);
            transactionRepository.save(transaction);
        }
    }

    public List<TransactionDTO> findTransactionsByUserIdLimited(Long id, Integer pageSize, Integer pageNumber) throws InstanceNotFoundException {
        Long accID = userService.getUserById(id).getAccount().getId();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());

        return transactionRepository.findAllByAccountId(accID, pageable).getContent().stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }
}
