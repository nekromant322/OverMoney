package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.MessageTelegram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageTelegramRepository extends JpaRepository<MessageTelegram, Integer> {
    void deleteByIdTransaction(UUID id);
}
