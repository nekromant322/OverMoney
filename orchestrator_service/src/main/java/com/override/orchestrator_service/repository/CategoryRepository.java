package com.override.orchestrator_service.repository;

import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.account.id = :id")
    List<Category> findAllByUserId(@Param("id") String accountId);

    @Query("SELECT c FROM Category c WHERE c.account.id = :id AND c.type = :type")
    List<Category> findAllByTypeAndAccId(@Param("id") Long accountId, @Param("type") Type type);
}
