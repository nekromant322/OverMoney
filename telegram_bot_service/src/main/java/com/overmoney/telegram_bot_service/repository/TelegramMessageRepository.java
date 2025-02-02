package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TelegramMessageRepository extends JpaRepository<TelegramMessage, Integer> {
    @Modifying
    @Query(value = "delete from telegram_message where id_transaction in (?1)", nativeQuery = true)
    void deleteByIdTransactions(List<UUID> ids);

    List<TelegramMessage> findByMessageIdAndChatId(Integer messageId, Long chatId);

    @Query(value = "SELECT * FROM telegram_message WHERE message_id = ?1 AND chat_id = ?2"
            , nativeQuery = true)
    List<TelegramMessage> findTgMessageIdsByMessageIdAndChatId(Integer messageId, Long chatId);

    @Query("SELECT DISTINCT tm.chatId FROM TelegramMessage tm")
    Set<Long> findUniqChatIds();
}
