package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.OverMoneyAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverMoneyAccountRepository extends CrudRepository<OverMoneyAccount, Long> {

    OverMoneyAccount findByChatId(String chatId);

    @Query(
            "SELECT o FROM OverMoneyAccount o JOIN UsersOverMoneyAccounts ua ON " +
                    "ua.user.id = :user_id"
    )
    List<OverMoneyAccount> findByUserId(@Param("user_id") Long id);
}
