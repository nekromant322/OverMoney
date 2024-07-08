package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TelegramMessageRepository extends JpaRepository<TelegramMessage, Integer> {
    void deleteByIdTransaction(UUID id);

    TelegramMessage findByMessageIdAndChatId(Integer messageId, Long chatId);

    @Query("SELECT DISTINCT tm.chatId FROM TelegramMessage tm")
    Set<Long> findUniqChatIds();
}
