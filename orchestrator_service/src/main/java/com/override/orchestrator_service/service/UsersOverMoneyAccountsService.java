package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.model.UsersOverMoneyAccounts;
import com.override.orchestrator_service.repository.UsersOverMoneyAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersOverMoneyAccountsService {
    @Autowired
    private UsersOverMoneyAccountsRepository usersOverMoneyAccountsRepository;

    public void save(User user, OverMoneyAccount account) {
        UsersOverMoneyAccounts usersOverMoneyAccounts = new UsersOverMoneyAccounts();
        usersOverMoneyAccounts.setUser(user);
        usersOverMoneyAccounts.setOverMoneyAccount(account);
        usersOverMoneyAccountsRepository.save(usersOverMoneyAccounts);
    }
}
