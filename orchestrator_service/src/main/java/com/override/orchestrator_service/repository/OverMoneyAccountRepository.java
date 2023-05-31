package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OverMoneyAccountRepository extends CrudRepository<OverMoneyAccount, Long> {

    OverMoneyAccount findByChatId(Long chatId);

    @Query("SELECT DISTINCT chatId from OverMoneyAccount WHERE id IN (SELECT DISTINCT t.account.id FROM Transaction t WHERE t.date > :minimalDate)")
    List<Long> findAllActivityUsersByAccId(@Param("minimalDate") LocalDateTime minimalDate);
}
