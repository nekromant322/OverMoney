package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.dto.TransactionMessageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.overmoney.telegram_bot_service.utils.TestFieldsUtil.generateTransactionDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorRequestServiceTest {

    @InjectMocks
    private OrchestratorRequestService orchestratorRequestService;

    @Mock
    private OrchestratorFeign orchestratorFeign;

    @Test
    public void sendTransactionTest() {
        TransactionMessageDTO transaction = generateTransactionDTO();

        orchestratorRequestService.sendTransaction(transaction);

        verify(orchestratorFeign, times(1)).sendTransaction(transaction);
    }

    @Test
    public void registerSingleAccountTest() {
        AccountDataDTO accountData = new AccountDataDTO();

        orchestratorRequestService.registerSingleAccount(accountData);

        verify(orchestratorFeign, times(1)).registerSingleAccount(accountData);
    }

    @Test
    public void registerGroupAccountAndMergeWithCategoriesAndWithoutTransactionsTest() {
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        orchestratorRequestService.registerGroupAccount(accountDataDTO);
        orchestratorRequestService.mergeWithCategoriesAndWithoutTransactions(accountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).registerGroupAccount(accountDataDTO);
        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesWithoutTransactions(accountDataDTO.getUserId());
    }

    @Test
    public void registerGroupAccountAndWithCategoriesAndTransactions() {
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        orchestratorRequestService.registerGroupAccount(accountDataDTO);
        orchestratorRequestService.mergeWithCategoryAndTransactions(accountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).registerGroupAccount(accountDataDTO);
        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesAndTransactions(accountDataDTO.getUserId());
    }

    @Test
    public void registerGroupAccount() {
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        orchestratorRequestService.registerGroupAccount(accountDataDTO);

        verify(orchestratorFeign, times(1)).registerGroupAccount(accountDataDTO);
    }

    @Test
    public void mergeWithCategoriesAndWithoutTransactions() {
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        orchestratorRequestService.mergeWithCategoriesAndWithoutTransactions(accountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesWithoutTransactions(accountDataDTO.getUserId());
    }

    @Test
    public void mergeWithCategoryAndTransactions() {
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        orchestratorRequestService.mergeWithCategoryAndTransactions(accountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesAndTransactions(accountDataDTO.getUserId());
    }

    @Test
    public void addNewChatMemberToAccount() {
        ChatMemberDTO newChatMember = new ChatMemberDTO();

        orchestratorRequestService.addNewChatMemberToAccount(newChatMember);

        verify(orchestratorFeign, times(1)).addNewChatMemberToAccount(newChatMember);
    }

}
