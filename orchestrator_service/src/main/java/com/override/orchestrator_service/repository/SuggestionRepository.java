package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Suggestion;
import com.override.orchestrator_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {
    Suggestion findSuggestionByTransaction(Transaction transaction);
}