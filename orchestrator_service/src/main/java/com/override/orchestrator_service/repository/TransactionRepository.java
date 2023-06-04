package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :id AND t.category.id is null")
    List<Transaction> findAllWithoutCategoriesByAccountId(@Param("id") Long accountId);

    @Modifying
    @Query("UPDATE Transaction t SET t.category.id = :newCategory WHERE t.category.id= :oldCategory")
    void updateCategoryId(@Param("oldCategory") Long oldCategoryId, @Param("newCategory") Long newCategoryId);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :id")
    Page<Transaction> findAllByAccountId(@Param("id") Long id, Pageable pageable);
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM transactions t WHERE t.account_id = :id ORDER BY t.date DESC LIMIT :limit OFFSET :start"
    )
    List<Transaction> findTransactionsLimited(@Param("id") Long id, @Param("limit") Integer limit, @Param("start") Integer start);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category.id= :categoryId")
    Long getSumTransactionsByCategoryId(@Param("categoryId") Long categoryId);
}