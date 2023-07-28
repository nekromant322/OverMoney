package com.overmoney.telegram_bot_service.feign;

import com.overmoney.telegram_bot_service.config.FeignConfiguration;
import com.override.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "orchestrator-service", configuration = FeignConfiguration.class)
public interface OrchestratorFeign {

    @PostMapping("/transaction")
    TransactionResponseDTO sendTransaction(@RequestBody TransactionMessageDTO transaction);

    @PostMapping("/account/register/single")
    void registerSingleAccount(@RequestBody AccountDataDTO accountData);

    @PostMapping("/account/register/group")
    void registerGroupAccount(@RequestBody AccountDataDTO accountDataDTO);

    @PostMapping("/account/merge/categories")
    void mergeAccountWithCategoriesWithoutTransactions(@RequestParam Long userId);

    @PostMapping("/account/merge/transactions")
    void mergeAccountWithCategoriesAndTransactions(@RequestParam Long userId);

    @PostMapping("/account/add/user")
    void addNewChatMemberToAccount(@RequestBody ChatMemberDTO chatMember);

    @PostMapping("/account/add/users")
    void addNewChatMembersToAccount(@RequestBody List<ChatMemberDTO> newChatMembers);

    @PostMapping("/account/remove/user")
    void removeChatMemberFromAccount(@RequestBody ChatMemberDTO chatMember);

    @GetMapping("/settings/backup/id")
    BackupUserDataDTO getBackup(@RequestParam Long userId);
}
