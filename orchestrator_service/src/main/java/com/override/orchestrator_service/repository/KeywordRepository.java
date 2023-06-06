package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {
    @Modifying
    @Query("UPDATE Keyword k SET k.category.id = :newCategory WHERE k.category.id= :oldCategory")
    void updateCategoryId(@Param("oldCategory") Long oldCategoryId, @Param("newCategory") Long newCategoryId);

    @Modifying
    @Query("DELETE FROM Keyword k WHERE k.category.id= :categoryId AND k.keyword= :value")
    void deleteKeywordByCategoryIdAndValue(@Param("categoryId") Long categoryId, @Param("value") String value);
}
