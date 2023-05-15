package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query(nativeQuery = true, value = "select * from categories where user_id = :userId")
    List<Category> findAllByUserId(@Param("userId") Long userId);

}
