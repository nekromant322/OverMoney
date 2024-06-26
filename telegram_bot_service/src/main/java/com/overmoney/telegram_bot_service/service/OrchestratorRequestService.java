package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public void registerGroupAccountAndMergeWithCategoriesAndWithoutTransactions(AccountDataDTO accountDataDTO) {
        registerGroupAccount(accountDataDTO);
        mergeWithCategoriesAndWithoutTransactions(accountDataDTO.getUserId());
    }

    public void registerGroupAccountAndWithCategoriesAndTransactions(AccountDataDTO accountDataDTO) {
        registerGroupAccount(accountDataDTO);
        mergeWithCategoryAndTransactions(accountDataDTO.getUserId());
    }

    public void registerGroupAccount(AccountDataDTO accountDataDTO) {
        orchestratorFeign.registerGroupAccount(accountDataDTO);
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

    public void removeChatMemberFromAccount(ChatMemberDTO leftChatMember) {
        orchestratorFeign.removeChatMemberFromAccount(leftChatMember);
    }

    public BackupUserDataDTO getBackup(Long userId) {
        return orchestratorFeign.getBackup(userId);
    }

    public void deleteTransactionById(UUID id) {
        orchestratorFeign.deleteTransactionById(id);
    }

    public TransactionResponseDTO submitTransactionForUpdate(TransactionMessageDTO transaction, UUID id) {
        return orchestratorFeign.submitTransactionForUpdate(transaction, id);
    }

    public TransactionResponseDTO submitTransactionForPatch(TransactionMessageDTO transaction, UUID id) {
        return orchestratorFeign.submitTransactionForPatch(transaction, id);
    }

    public TransactionDTO getTransactionById(UUID id) {
        return orchestratorFeign.getTransactionById(id);
    }
}
