package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {
    @Modifying
    @Query(value = "DELETE FROM suggestions s WHERE s.transaction_id IN (:ids)", nativeQuery = true)
    void deleteByTransactionIds(@Param("ids") List<UUID> ids);

    List<Suggestion> findAllByAlgorithm(String algorithm);
}