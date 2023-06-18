package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.MergeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MergeRequestRepository extends JpaRepository<MergeRequest, Long> {

    MergeRequest getMergeRequestByChatId(Long chatId);

    @Modifying
    @Query("UPDATE MergeRequest mr SET mr.completed = true WHERE mr.chatId = :chatId")
    void updateMergeRequestCompletionByChatId(@Param("chatId") Long chatId);
}
