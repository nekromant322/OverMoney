package com.override.orchestrator_service.repository;

import com.override.dto.AnalyticsMonthlyIncomeForCategoryDTO;
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

    List<Transaction> findAllByAccountId(Long accountId);

    @Modifying
    @Query("UPDATE Transaction t SET t.category.id = :newCategory WHERE t.category.id= :oldCategory")
    void updateCategoryId(@Param("oldCategory") Long oldCategoryId, @Param("newCategory") Long newCategoryId);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :id")
    Page<Transaction> findAllByAccountId(@Param("id") Long id, Pageable pageable);

    @Modifying
    @Query("UPDATE Transaction t SET t.category.id = :newCategory " +
            "WHERE t.account.id= :accId AND t.category.id IS NULL AND t.message = :message")
    void updateCategoryIdWhereCategoryIsNull(@Param("newCategory") Long newCategoryId,
                                             @Param("message") String message,
                                             @Param("accId") Long accId);

    @Query("SELECT t.account.id FROM Transaction t WHERE t.id= :transactionId")
    Long findAccountIdByTransactionId(@Param("transactionId") UUID transactionId);

    @Modifying
    @Query("UPDATE Transaction t SET t.account.id = :newAccountId WHERE t.account.id = :oldAccountId")
    void updateAccountId(@Param("oldAccountId") Long oldAccountId, @Param("newAccountId") Long newAccountId);

    @Modifying
    @Query("UPDATE Transaction t SET t.category.id = NULL " +
            "WHERE t.account.id= :accountId AND t.message = :message")
    void removeCategoryIdFromTransactionsWithSameMessage(@Param("message") String message,
                                                         @Param("accountId") Long accountId);

    @Query("SELECT DISTINCT EXTRACT(year from t.date) from Transaction t WHERE t.account.id = :accountId")
    List<Integer> findAvailableYearsForAccountByAccountId(@Param("accountId") Long accountId);

    @Query(value = "select new com.override.dto.AnalyticsMonthlyIncomeForCategoryDTO(SUM(t.amount), c.name, cast(MONTH(t.date) as int)) " +
            "from Transaction t join Category c on t.category.id = c.id " +
            "where t.account.id = :accountId and YEAR(t.date) = :year and c.type = 0 " +
            "group by c.id, MONTH(t.date), c.name")
    List<AnalyticsMonthlyIncomeForCategoryDTO> findMonthlyIncomeStatisticsByYearAndAccountId(@Param("accountId") Long accountId,
                                                                                             @Param("year") Integer year);

    void deleteAllByAccountId(Long accountId);
}