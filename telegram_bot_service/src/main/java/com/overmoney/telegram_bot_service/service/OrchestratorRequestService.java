package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.*;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrchestratorRequestService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    public TransactionResponseDTO sendTransaction(TransactionMessageDTO transaction) {
        return orchestratorFeign.sendTransaction(transaction);
    }

    public boolean registerSingleAccount(AccountDataDTO accountData) {
        try {
            orchestratorFeign.registerSingleAccount(accountData);
            return true;
        } catch (FeignException e) {
            log.error("Ошибка при регистрации аккаунта: " + e.getMessage());
            return false;
        }
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

    public TransactionResponseDTO submitTransactionForPatch(TransactionMessageDTO transaction, UUID id) {
        return orchestratorFeign.submitTransactionForPatch(transaction, id);
    }

    public TransactionDTO getTransactionById(UUID id) {
        return orchestratorFeign.getTransactionById(id);
    }
}
