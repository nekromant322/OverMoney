package com.overmoney.telegram_bot_service.feign;

import com.override.dto.AccountDataDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="orchestrator-service")
public interface OrchestratorFeign {

    @PostMapping("/transaction")
    TransactionResponseDTO sendTransaction(@RequestBody TransactionMessageDTO transaction);

    @PostMapping("/account/register")
    void registerAccount(@RequestBody AccountDataDTO accountData);

    @PostMapping("/account/merge/categories")
    void mergeAccountWithCategoriesWithoutTransactions(@RequestParam Long userId);

    @PostMapping("/account/merge/transactions")
    void mergeAccountWithCategoriesAndTransactions(@RequestParam Long userId);
}
