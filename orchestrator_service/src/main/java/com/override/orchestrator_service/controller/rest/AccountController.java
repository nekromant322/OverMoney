package com.override.orchestrator_service.controller.rest;

import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import com.override.orchestrator_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private OverMoneyAccountService accountService;

    @Autowired
    private UserService userService;

    @PostMapping("/register/single")
    public void registerSingleAccount(@RequestBody AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        userService.saveUser(accountDataDTO);
        accountService.registerSingleOverMoneyAccount(accountDataDTO);
    }

    /**
     * метод добавлен для возможного расширения функционала бота
     * @param accountDataDTO - данные аккаунта (предполагается, что туда можно будет добавить
     *                       список юзеров или юзернеймов)
     * @throws InstanceNotFoundException
     */
    @PostMapping("/register/group")
    public void registerGroupAccount(@RequestBody AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        accountService.registerGroupOverMoneyAccount(accountDataDTO);
    }

    @PostMapping("/add/users")
    public void addNewChatMembersToAccount(@RequestBody List<ChatMemberDTO> chatMemberDTOList) {
        accountService.addNewChatMembersToAccount(chatMemberDTOList);
    }

    @PostMapping("/add/user")
    public void addNewChatMemberToAccount(@RequestBody ChatMemberDTO chatMemberDTO) {
        accountService.addNewChatMemberToAccount(chatMemberDTO);
    }

    /**
     * Метод POST-запроса, переносящий только категории в новый аккаунт
     */
    @PostMapping("/merge/categories")
    public void mergeCategories(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndWithoutTransactions(userId);
    }

    /**
     * Метод POST-запроса, переносящий и категории, и транзакции в новый аккаунт
     */
    @PostMapping("/merge/transactions")
    public void mergeTransactions(@RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndTransactions(userId);
    }
}
