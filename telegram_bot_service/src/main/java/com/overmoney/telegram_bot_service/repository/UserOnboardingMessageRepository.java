package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.UserOnboardingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserOnboardingMessageRepository extends JpaRepository<UserOnboardingMessage, Long> {
    boolean existsByUserIdAndOnboardingMessageId(Long userId, UUID onboardingMessageId);

    void deleteAllByOnboardingMessageIdIn(Set<UUID> onboardingMessageIds);
}

