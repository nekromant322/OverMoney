package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverMoneyAccountRepository extends CrudRepository<OverMoneyAccount, Long> {

    OverMoneyAccount findByChatId(Long chatId);
}
