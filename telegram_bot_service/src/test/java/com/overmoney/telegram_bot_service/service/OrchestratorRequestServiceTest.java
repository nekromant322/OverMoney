package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.GroupAccountDataDTO;
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
        GroupAccountDataDTO groupAccountDataDTO = new GroupAccountDataDTO();

        orchestratorRequestService.registerGroupAccount(groupAccountDataDTO);
        orchestratorRequestService.mergeWithCategoriesAndWithoutTransactions(groupAccountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).registerGroupAccount(groupAccountDataDTO);
        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesWithoutTransactions(groupAccountDataDTO.getUserId());
    }

    @Test
    public void registerGroupAccountAndWithCategoriesAndTransactions() {
        GroupAccountDataDTO groupAccountDataDTO = new GroupAccountDataDTO();

        orchestratorRequestService.registerGroupAccount(groupAccountDataDTO);
        orchestratorRequestService.mergeWithCategoryAndTransactions(groupAccountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).registerGroupAccount(groupAccountDataDTO);
        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesAndTransactions(groupAccountDataDTO.getUserId());
    }

    @Test
    public void registerGroupAccount() {
        GroupAccountDataDTO groupAccountDataDTO = new GroupAccountDataDTO();

        orchestratorRequestService.registerGroupAccount(groupAccountDataDTO);

        verify(orchestratorFeign, times(1)).registerGroupAccount(groupAccountDataDTO);
    }

    @Test
    public void mergeWithCategoriesAndWithoutTransactions() {
        GroupAccountDataDTO groupAccountDataDTO = new GroupAccountDataDTO();

        orchestratorRequestService.mergeWithCategoriesAndWithoutTransactions(groupAccountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesWithoutTransactions(groupAccountDataDTO.getUserId());
    }

    @Test
    public void mergeWithCategoryAndTransactions() {
        GroupAccountDataDTO groupAccountDataDTO = new GroupAccountDataDTO();

        orchestratorRequestService.mergeWithCategoryAndTransactions(groupAccountDataDTO.getUserId());

        verify(orchestratorFeign, times(1)).mergeAccountWithCategoriesAndTransactions(groupAccountDataDTO.getUserId());
    }

    @Test
    public void addNewChatMemberToAccount() {
        ChatMemberDTO newChatMember = new ChatMemberDTO();

        orchestratorRequestService.addNewChatMemberToAccount(newChatMember);

        verify(orchestratorFeign, times(1)).addNewChatMemberToAccount(newChatMember);
    }

}
