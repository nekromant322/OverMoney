package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
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

        return transactionRepository.findAllByAccountId(accID, pageable).getContent().stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
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
}
