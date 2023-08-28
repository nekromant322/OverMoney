package com.override.orchestrator_service.service;

import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.config.RecentActivityProperties;
import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.KeywordRepository;
import com.override.orchestrator_service.repository.OverMoneyAccountRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
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
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private TelegramUtils telegramUtils;

    public List<OverMoneyAccount> getAllAccounts() {
        return (List<OverMoneyAccount>) overMoneyAccountRepository.findAll();
    }

    public int getGroupAccountsCount() {
        return overMoneyAccountRepository.getGroupAccountsCount();
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

    @Transactional
    public void addNewChatMembersToAccount(List<ChatMemberDTO> chatMemberDTOList) {
        chatMemberDTOList.forEach(this::addNewChatMemberToAccount);
    }

    public void addNewChatMemberToAccount(ChatMemberDTO chatMemberDTO) {
        OverMoneyAccount account = getOverMoneyAccountByChatId(chatMemberDTO.getChatId());
        User user = userMapper.mapChatMemberDTOToUser(chatMemberDTO);
        user.setAccount(account);
        userService.saveUser(user);
    }

    public void removeChatMemberFromAccount(ChatMemberDTO chatMemberDTO) throws InstanceNotFoundException {
        User user = userService.getUserById(chatMemberDTO.getUserId());
        OverMoneyAccount overMoneyAccount = overMoneyAccountRepository.findByChatId(chatMemberDTO.getUserId());

        if (overMoneyAccount != null) {
            user.setAccount(overMoneyAccount);
            userService.saveUser(user);
        } else {
            user.setAccount(null);
            userService.saveUser(user);
        }
    }

    public void registerSingleOverMoneyAccount(AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        User user = userService.getUserById(accountDataDTO.getUserId());
        OverMoneyAccount overMoneyAccount = overMoneyAccountRepository.findByChatId(accountDataDTO.getChatId());

        if (overMoneyAccount != null) {
            user.setAccount(overMoneyAccountRepository.findByChatId(accountDataDTO.getChatId()));
            userService.saveUser(user);
            return;
        }

        overMoneyAccount = OverMoneyAccount.builder()
                .chatId(accountDataDTO.getChatId())
                .users(getUser(accountDataDTO.getUserId()))
                .build();
        user.setAccount(overMoneyAccount);
        saveOverMoneyAccount(overMoneyAccount);
    }

    public void registerSingleOverMoneyAccount(Principal principal) throws InstanceNotFoundException {
        Long telegramId = telegramUtils.getTelegramId(principal);
        User user = userService.getUserById(telegramId);
        OverMoneyAccount overMoneyAccount = OverMoneyAccount.builder()
                .chatId(telegramId)
                .users(getUser(telegramId))
                .build();
        saveOverMoneyAccount(overMoneyAccount);
        user.setAccount(overMoneyAccount);
        userService.saveUser(user);
    }

    public void registerGroupOverMoneyAccount(AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = OverMoneyAccount.builder()
                .chatId(accountDataDTO.getChatId())
                .users(getUser(accountDataDTO.getUserId()))
                .build();
        User user = userService.getUserById(accountDataDTO.getUserId());
        user.setAccount(overMoneyAccount);
        saveOverMoneyAccount(overMoneyAccount);
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

    public void deletingAllTransactionsCategoriesKeywordsByAccountId(Long accountId) {
        keywordRepository.deleteAllByKeywordId_AccountId(accountId);
        transactionRepository.deleteAllByAccountId(accountId);
        categoryRepository.deleteAllByAccountId(accountId);
    }
}
