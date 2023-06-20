package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.dto.GroupAccountDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrchestratorRequestService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    public TransactionResponseDTO sendTransaction(TransactionMessageDTO transaction) {
       return orchestratorFeign.sendTransaction(transaction);
    }

    public void registerSingleAccount(AccountDataDTO accountData) {
        orchestratorFeign.registerSingleAccount(accountData);
    }

    public void registerGroupAccountAndMergeWithCategoriesAndWithoutTransactions(GroupAccountDataDTO groupAccountData) {
        registerGroupAccount(groupAccountData);
        mergeWithCategoriesAndWithoutTransactions(groupAccountData.getUserId());
    }

    public void registerGroupAccountAndWithCategoriesAndTransactions(GroupAccountDataDTO groupAccountData) {
        registerGroupAccount(groupAccountData);
        mergeWithCategoryAndTransactions(groupAccountData.getUserId());
    }

    public void registerGroupAccount(GroupAccountDataDTO groupAccountData) {
        orchestratorFeign.registerGroupAccount(groupAccountData);
    }

    public void mergeWithCategoriesAndWithoutTransactions(Long userId) {
        orchestratorFeign.mergeAccountWithCategoriesWithoutTransactions(userId);
    }

    public void mergeWithCategoryAndTransactions(Long userId) {
        orchestratorFeign.mergeAccountWithCategoriesAndTransactions(userId);
    }

    public void addNewChatMembersToAccount(List<ChatMemberDTO> newChatMembers) {
        orchestratorFeign.addNewChatMembersToAccount(newChatMembers);
    }

    public void addNewChatMemberToAccount(ChatMemberDTO newChatMember) {
        orchestratorFeign.addNewChatMemberToAccount(newChatMember);
    }
}
