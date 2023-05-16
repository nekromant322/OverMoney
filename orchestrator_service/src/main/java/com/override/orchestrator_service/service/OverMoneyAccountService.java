package com.override.orchestrator_service.service;

import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.OverMoneyAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class OverMoneyAccountService {
    @Autowired
    private OverMoneyAccountRepository overMoneyAccountRepository;

    public List<OverMoneyAccount> getAllAccounts() {
        return (List<OverMoneyAccount>) overMoneyAccountRepository.findAll();
    }

    public List<OverMoneyAccount> getAllUserAccounts(User user) {
        return overMoneyAccountRepository.findByUserId(user.getId());
    }

    public void saveOverMoneyAccount(OverMoneyAccount overMoneyAccount) {
        overMoneyAccountRepository.save(overMoneyAccount);
    }

    public void updateOverMoneyAccount(OverMoneyAccount overMoneyAccount, String chatId) throws InstanceNotFoundException {
        OverMoneyAccount foundAccount = getOverMoneyAccountByChatId(chatId);
        foundAccount.setUsersOverMoneyAccounts(overMoneyAccount.getUsersOverMoneyAccounts());
        foundAccount.setCategories(overMoneyAccount.getCategories());
        foundAccount.setTransactions(overMoneyAccount.getTransactions());
        overMoneyAccountRepository.save(foundAccount);
    }

    public OverMoneyAccount getOverMoneyAccountByChatId(String chatId) throws InstanceNotFoundException {
        return overMoneyAccountRepository.findByChatId(chatId);
    }

    public void updateOverMoneyAccount(OverMoneyAccount overMoneyAccount, Long id) throws InstanceNotFoundException {
        OverMoneyAccount foundAccount = getOverMoneyAccountById(id);
        foundAccount.setChatId(overMoneyAccount.getChatId());
        foundAccount.setUsersOverMoneyAccounts(overMoneyAccount.getUsersOverMoneyAccounts());
        foundAccount.setCategories(overMoneyAccount.getCategories());
        foundAccount.setTransactions(overMoneyAccount.getTransactions());
        overMoneyAccountRepository.save(foundAccount);
    }

    public OverMoneyAccount getOverMoneyAccountById(Long id) throws InstanceNotFoundException {
        return overMoneyAccountRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException("OverMoneyAccount with id " + id + " does not exist"));
    }

    public void deleteOverMoneyAccountById(Long id) {
        overMoneyAccountRepository.deleteById(id);
    }
}
