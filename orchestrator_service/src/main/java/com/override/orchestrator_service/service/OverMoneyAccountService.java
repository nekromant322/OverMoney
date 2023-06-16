package com.override.orchestrator_service.service;

import com.override.orchestrator_service.config.RecentActivityProperties;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.OverMoneyAccountRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OverMoneyAccountService {
    @Autowired
    private OverMoneyAccountRepository overMoneyAccountRepository;
    @Autowired
    private RecentActivityProperties recentActivityProperties;
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBotFeign telegramBotFeign;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private final int ACCOUNT_DEFINER = 0;

    public List<OverMoneyAccount> getAllAccounts() {
        return (List<OverMoneyAccount>) overMoneyAccountRepository.findAll();
    }

    public List<Long> getAllActivityUsers() {
        LocalDateTime minimalDate = LocalDateTime.now().minusDays(recentActivityProperties.getActivityDays());
        return overMoneyAccountRepository.findAllActivityUsersByAccId(minimalDate);
    }

    @Transactional
    public void mergeToGroupAccountWithCategoriesAndWithoutTransactions(Long userId) {
        OverMoneyAccount oldAccount = getOldAccount(userId);
        OverMoneyAccount newAccount = getNewAccount(userId);

        updateAccountCategories(oldAccount, newAccount);
    }

    @Transactional
    public void mergeToGroupAccountWithCategoriesAndTransactions(Long userId) {
        OverMoneyAccount oldAccount = getOldAccount(userId);
        OverMoneyAccount newAccount = getNewAccount(userId);

        updateAccount(oldAccount, newAccount);
    }

    public OverMoneyAccount getOldAccount(Long userId) {
        return getOverMoneyAccountByChatId(userId);
    }

    public OverMoneyAccount getNewAccount(Long userId) {
        return overMoneyAccountRepository.findNewAccountByUserId(userId);
    }

    @Transactional
    public void updateAccount(OverMoneyAccount oldAccount, OverMoneyAccount newAccount) {
        updateAccountCategories(oldAccount, newAccount);
        updateAccountTransactions(oldAccount, newAccount);
    }

    public void updateAccountCategories(OverMoneyAccount oldAccount, OverMoneyAccount newAccount) {
        categoryRepository.updateAccountId(oldAccount.getId(), newAccount.getId());
    }

    public void updateAccountTransactions(OverMoneyAccount oldAccount, OverMoneyAccount newAccount) {
        transactionRepository.updateAccountId(oldAccount.getId(), newAccount.getId());
    }

    public void registerOverMoneyAccount(Long chatId, Long userId) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = OverMoneyAccount.builder()
                .chatId(chatId)
                .users(getUser(userId))
                .build();
        User user = userService.getUserById(userId);
        user.setAccount(overMoneyAccount);
        saveOverMoneyAccount(overMoneyAccount);
        if (chatId < ACCOUNT_DEFINER) {
            telegramBotFeign.sendMergeRequest(userId);
        }
    }

    private Set<User> getUser(Long userId) throws InstanceNotFoundException {
        User user = userService.getUserById(userId);
        Set<User> accountUsers = new HashSet<>();
        accountUsers.add(user);
        return accountUsers;
    }

    public void saveOverMoneyAccount(OverMoneyAccount overMoneyAccount) {
        overMoneyAccountRepository.save(overMoneyAccount);
    }

    public OverMoneyAccount getOverMoneyAccountByChatId(Long chatId) {
        return overMoneyAccountRepository.findByChatId(chatId);
    }

    public OverMoneyAccount getAccountByUserId(Long id) throws InstanceNotFoundException {
        return userService.getUserById(id).getAccount();
    }

    public OverMoneyAccount getOverMoneyAccountById(Long id) throws InstanceNotFoundException {
        return overMoneyAccountRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException("OverMoneyAccount with id " + id + " does not exist"));
    }

    public void deleteOverMoneyAccountById(Long id) {
        overMoneyAccountRepository.deleteById(id);
    }
}
