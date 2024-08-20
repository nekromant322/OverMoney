package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.KeywordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, KeywordId> {

    @Query("SELECT k FROM Keyword k WHERE k.keywordId.accountId = :accountId")
    List<Keyword> findAllByOverMoneyAccount(Long accountId);

    @Modifying
    @Query("UPDATE Keyword k SET k.category.id = :newCategory WHERE k.category.id= :oldCategory")
    void updateCategoryId(@Param("oldCategory") Long oldCategoryId, @Param("newCategory") Long newCategoryId);

    Optional<Keyword> findByKeywordId(KeywordId keywordId);

    List<Keyword> findAllByKeywordId_Name(String name);

    void deleteByKeywordId(KeywordId keywordId);

    @Modifying
    @Query("UPDATE Keyword k SET k.category.id = null WHERE k.keywordId = :keywordId")
    void removeCategoryId(@Param("keywordId") KeywordId keywordId);

    @Transactional
    void deleteAllByKeywordId_AccountId(Long accountId);

    @Modifying
    @Query("DELETE FROM Keyword k WHERE k.keywordId.name =:name AND k.keywordId.accountId = :id")
    void deleteByNameAndAccountIdWithJpqlQuery(@Param("name") String name, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Keyword k WHERE k.lastUsed < :maxDate AND k.usageCount <= :minUsageCount")
    void deleteDeprecatedKeywords(@Param("maxDate") LocalDateTime maxDate, @Param("minUsageCount") int minUsageCount);
}