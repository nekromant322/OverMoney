package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.OnboardingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface OnboardingMessageRepository extends JpaRepository<OnboardingMessage, UUID> {
    @Query("select om.id from OnboardingMessage om")
    Set<UUID> findAllIds();
}

