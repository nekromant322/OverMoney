package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.TinkoffInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestTinkoffInfoRepository extends JpaRepository<TinkoffInfo, Long> {

    Optional<TinkoffInfo> findTinkoffInfoById(Long userId);

    Optional<TinkoffInfo> findByToken(String token);
}
