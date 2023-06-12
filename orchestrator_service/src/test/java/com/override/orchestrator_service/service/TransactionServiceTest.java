package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import com.override.orchestrator_service.exception.TransactionNotFoundException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private TransactionMapper transactionMapper;

    @Test
    public void setTransactionCategoryThrowExceptionWhenCategoryNotFound() {
        final Category category = new Category();
        category.setId(12345L);
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(categoryService.getCategoryById(category.getId())).thenThrow(CategoryNotFoundException.class);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        assertThrows(CategoryNotFoundException.class, () ->
                transactionService.setTransactionCategory(transaction.getId(), category.getId()));
    }

    @Test
    public void setTransactionCategoryThrowExceptionWhenTransactionNotFound() {
        final Category category = new Category();
        category.setId(12345L);
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.setTransactionCategory(transaction.getId(), category.getId()));
    }

    @Test
    public void transactionRepositorySaveTransactionWhenCategoryAndTransactionFound() {
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        transactionService.saveTransaction(transaction);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void setTransactionCategorySaveTransactionWhenCategoryAndTransactionFound() {
        final Category category = new Category();
        category.setId(12345L);
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(categoryService.getCategoryById(category.getId())).thenReturn(category);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.setTransactionCategory(transaction.getId(), category.getId());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
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
    public void setTransactionNoCategorySaveTransactionWhenTransactionFound() {
        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.setTransactionNoCategory(transaction.getId());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void setTransactionNoCategoryThrowExceptionWhenTransactionNotFound() {

        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.setTransactionNoCategory(transaction.getId()));
    }

    @Test
    public void setTransactionsWithSameMessageAndAccountIdNoCategoryThrowExceptionWhenTransactionNotFound() {

        final Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.setTransactionsWithSameMessageAndAccountIdNoCategory(transaction.getId()));
    }


}
