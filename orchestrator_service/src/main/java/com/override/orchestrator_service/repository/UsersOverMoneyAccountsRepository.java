package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.UsersOverMoneyAccounts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersOverMoneyAccountsRepository extends CrudRepository<UsersOverMoneyAccounts, UUID> {
}
