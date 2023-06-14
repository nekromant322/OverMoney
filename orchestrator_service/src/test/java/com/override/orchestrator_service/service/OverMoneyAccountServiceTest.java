package com.override.orchestrator_service.service;

import com.override.orchestrator_service.config.RecentActivityProperties;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.OverMoneyAccountRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OverMoneyAccountServiceTest {

    @InjectMocks
    private OverMoneyAccountService accountService;
    @Mock
    private OverMoneyAccountRepository accountRepository;
    @Mock
    private RecentActivityProperties recentActivityProperties;
    @Mock
    private UserService userService;
    @Mock
    private TelegramBotFeign telegramBotFeign;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void mergeToGroupAccountWithCategoriesTest() {
        OverMoneyAccount oldAccount = TestFieldsUtil.generateTestAccount();
        oldAccount.setUsers(null);
        Set<Category> categories = oldAccount.getCategories();

        OverMoneyAccount newAccount = TestFieldsUtil.generateTestAccount();
        newAccount.setCategories(null);
        newAccount.setId(-123L);

        accountService.updateAccount(newAccount, categories);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void mergeToGroupAccountWithCategoriesAndTransactionsTest() {
        OverMoneyAccount oldAccount = TestFieldsUtil.generateTestAccount();
        oldAccount.setUsers(null);
        Set<Category> categories = oldAccount.getCategories();

        OverMoneyAccount newAccount = TestFieldsUtil.generateTestAccount();
        newAccount.setCategories(null);
        newAccount.setId(-123L);

        accountService.updateAccount(newAccount, categories);

        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    public void registerSingleOverMoneyAccountTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        User user = account.getUsers().iterator().next();
        Long userId = user.getId();
        Long chatId = account.getChatId();

        when(userService.getUserById(any())).thenReturn(user);
        when(accountRepository.save(any())).thenReturn(account);

        accountService.registerOverMoneyAccount(chatId, userId);

        verify(accountRepository, times(1)).save(any(OverMoneyAccount.class));
    }

    @Test
    public void registerGroupOverMoneyAccountTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        account.setChatId(-123L);
        User user = account.getUsers().iterator().next();
        Long userId = user.getId();
        Long chatId = account.getChatId();

        when(userService.getUserById(any())).thenReturn(user);
        when(accountRepository.save(any())).thenReturn(account);

        accountService.registerOverMoneyAccount(chatId, userId);

        verify(accountRepository, times(1)).save(any(OverMoneyAccount.class));
        verify(telegramBotFeign, times(1)).sendMergeRequest(userId);
    }
}
