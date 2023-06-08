package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.KeywordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {
    @Modifying
    @Query("UPDATE Keyword k SET k.category.id = :newCategory WHERE k.category.id= :oldCategory")
    void updateCategoryId(@Param("oldCategory") Long oldCategoryId, @Param("newCategory") Long newCategoryId);

    Keyword findByKeywordId(KeywordId keywordId);

    List<Keyword> findAllByKeywordId_AccountId(Long accountId);

    List<Keyword> findAllByKeywordId_Name(String name);

    void deleteByKeywordId(KeywordId keywordId);
}