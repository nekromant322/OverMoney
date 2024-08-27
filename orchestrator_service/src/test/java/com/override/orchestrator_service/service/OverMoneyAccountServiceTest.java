package com.override.orchestrator_service.service;

import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.config.RecentActivityProperties;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.mapper.UserMapper;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.repository.OverMoneyAccountRepository;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.util.TelegramUtils;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
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
    @Mock
    private UserMapper userMapper;

    @Mock
    private TelegramUtils telegramUtils;

    @Test
    public void mergeToGroupAccountWithCategoriesAndWithoutTransactionsTest() {
        OverMoneyAccount oldAccount = TestFieldsUtil.generateTestAccount();

        OverMoneyAccount newAccount = TestFieldsUtil.generateTestAccount();
        newAccount.setCategories(null);
        newAccount.setId(-123L);

        accountService.updateAccountCategories(oldAccount, newAccount);

        verify(categoryRepository, times(1)).updateAccountId(oldAccount.getId(), newAccount.getId());
    }

    @Test
    public void mergeToGroupAccountWithCategoriesAndTransactionsTest() {
        OverMoneyAccount oldAccount = TestFieldsUtil.generateTestAccount();
        oldAccount.setTransactions(Set.of(TestFieldsUtil.generateTestTransaction()));

        OverMoneyAccount newAccount = TestFieldsUtil.generateTestAccount();
        newAccount.setCategories(null);
        newAccount.setId(-123L);

        accountService.updateAccount(oldAccount, newAccount);

        verify(categoryRepository, times(1)).updateAccountId(oldAccount.getId(), newAccount.getId());
        verify(transactionRepository, times(1)).updateAccountId(oldAccount.getId(), newAccount.getId());
    }

    @Test
    public void registerSingleOverMoneyAccountTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        User user = account.getUsers().iterator().next();
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        when(userService.getUserById(any())).thenReturn(user);
        when(accountRepository.save(any())).thenReturn(account);

        accountService.registerSingleOverMoneyAccount(accountDataDTO);

        verify(accountRepository, times(1)).save(any(OverMoneyAccount.class));
    }

    @Test
    public void RegisterSingleOverMoneyAccountWeb() throws InstanceNotFoundException {
        MockitoAnnotations.openMocks(this);

        Principal principal = Mockito.mock(Principal.class);
        when(telegramUtils.getTelegramId(principal)).thenReturn(123L);

        User user = new User();
        when(userService.getUserById(123L)).thenReturn(user);

        OverMoneyAccount savedAccount = new OverMoneyAccount();
        when(accountRepository.save(any(OverMoneyAccount.class))).thenReturn(savedAccount);

        accountService.registerSingleOverMoneyAccount(principal);

        verify(telegramUtils).getTelegramId(principal);
        verify(userService, times(2)).getUserById(123L);
        verify(accountRepository).save(any(OverMoneyAccount.class));
        verify(userService).saveUser(user);
    }

    @Test
    public void registerGroupOverMoneyAccountTest() throws InstanceNotFoundException {
        OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        account.setChatId(-123L);
        User user = account.getUsers().iterator().next();
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        when(userService.getUserById(any())).thenReturn(user);
        when(accountRepository.save(any())).thenReturn(account);

        accountService.registerGroupOverMoneyAccount(accountDataDTO);

        verify(accountRepository, times(1)).save(any(OverMoneyAccount.class));
    }

    @Test
    public void addNewChatMemberToAccountTest() {
        ChatMemberDTO chatMemberDTO = ChatMemberDTO.builder()
                .chatId(-123L)
                .lastName("")
                .firstName("")
                .username("etozhealexis")
                .build();
        User user = User.builder()
                .id(123L)
                .firstName("")
                .lastName("")
                .username("etozhealexis")
                .build();

        when(userMapper.mapChatMemberDTOToUser(chatMemberDTO)).thenReturn(user);
        accountService.addNewChatMemberToAccount(chatMemberDTO);

        verify(userMapper, times(1)).mapChatMemberDTOToUser(chatMemberDTO);
        verify(userService, times(1)).saveUser(user);
    }
    @Test
    public void removeChatMemberToAccountTest() throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = OverMoneyAccount.builder()
                .chatId(-123L)
                .id(123L)
                .build();
        ChatMemberDTO chatMemberDTO = ChatMemberDTO.builder()
                .chatId(-123L)
                .userId(123L)
                .lastName("")
                .firstName("")
                .username("borisat")
                .build();
        User user = User.builder()
                .id(123L)
                .firstName("")
                .lastName("")
                .username("borisat")
                .account(overMoneyAccount)
                .build();

        when(userService.getUserById(chatMemberDTO.getUserId())).thenReturn(user);
        accountService.removeChatMemberFromAccount(chatMemberDTO);

        verify(userService, times(1)).getUserById(chatMemberDTO.getUserId());
        verify(userService, times(1)).saveUser(user);
    }
}
