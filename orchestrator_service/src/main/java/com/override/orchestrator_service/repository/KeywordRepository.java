package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

}