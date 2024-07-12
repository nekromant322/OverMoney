package com.override.orchestrator_service.controller.rest;

import com.override.dto.AccountDataDTO;
import com.override.dto.ChatMemberDTO;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import com.override.orchestrator_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/account")
@Tag(name = "контроллер аккаунтов", description = "Операции с пользовательскими аккаунтами")
public class AccountController {
    @Autowired
    private OverMoneyAccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping("/register/single")
    @Operation(summary = "Зарегистрировать телеграм-пользователя", description = "Регестрирует новый аккаунт в OverMoney " +
            "используя профиль телеграма")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Регистрация успешна"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
    })
    public void registerSingleAccount(Principal principal) throws InstanceNotFoundException {
        accountService.registerSingleOverMoneyAccount(principal);
    }

    @PostMapping("/register/single")
    @Operation(summary = "Зарегистрировать нового пользователя", description = "Регестрирует новый аккаунт в OverMoney")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Регистрация успешна"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Аккаунт уже зарегистрирован"),
    })
    public void registerSingleAccount(@Parameter(description = "данные аккаунта")
                                          @RequestBody AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        userService.saveUser(accountDataDTO);
        accountService.registerSingleOverMoneyAccount(accountDataDTO);
    }

    @PostMapping("/register/group")
    @Operation(summary = "Зарегистрировать групповой аккаунт", description = "Регистрирует групповой аккаунт в OverMoney")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация успешна"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Аккаунт уже зарегистрирован"),
    })
    public void registerGroupAccount(@Parameter(description = "Данные аккаунта (предполагается, что туда можно будет " +
            "добавить список юзеров или юзернеймов)") @RequestBody AccountDataDTO accountDataDTO) throws InstanceNotFoundException {
        accountService.registerGroupOverMoneyAccount(accountDataDTO);
    }

    @PostMapping("/add/users")
    @Operation(summary = "Добавить пользователей в групповой аккаунт", description = "Добавляет в групповой аккаунт в OverMoney" +
            "пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Добавление успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
    })
    public void addNewChatMembersToAccount(@Parameter(description = "список данных пользователей")
                                               @RequestBody List<ChatMemberDTO> chatMemberDTOList) {
        accountService.addNewChatMembersToAccount(chatMemberDTOList);
    }

    @PostMapping("/add/user")
    @Operation(summary = "Добавить пользователя в групповой аккаунт", description = "Добавляет в групповой аккаунт в OverMoney" +
            "пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Добавление успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
    })
    public void addNewChatMemberToAccount(@Parameter(description = "данные пользователя") @RequestBody ChatMemberDTO chatMemberDTO) {
        accountService.addNewChatMemberToAccount(chatMemberDTO);
    }

    @PostMapping("/remove/user")
    @Operation(summary = "Удалить пользователя из группового аккаунта", description = "Удаляет из группового аккаунта в OverMoney" +
            "пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удалено успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
    })
    public void removeChatMemberFromAccount(@Parameter(description = "данные пользователя") @RequestBody ChatMemberDTO chatMemberDTO) throws InstanceNotFoundException {
        accountService.removeChatMemberFromAccount(chatMemberDTO);
    }

    @PostMapping("/merge/categories")
    @Operation(summary = "Перенести категории в новый аккаунт", description = "Перенести только категории в новый аккаунт " +
            "пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перенесено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
    })
    public void mergeCategories(@Parameter(description = "ID пользователя") @RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndWithoutTransactions(userId);
    }

    @PostMapping("/merge/transactions")
    @Operation(summary = "Перенести категории в новый аккаунт", description = "Перенести только категории в новый аккаунт " +
            "пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перенесено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public void mergeTransactions(@Parameter(description = "ID пользователя") @RequestParam Long userId) {
        accountService.mergeToGroupAccountWithCategoriesAndTransactions(userId);
    }

    @GetMapping("/count")
    @Operation(summary = "Получить количество групповых аккаунтов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество получено успешно"),
    })
    public int getAccountsCount() {
        return accountService.getGroupAccountsCount();
    }

    @GetMapping("/users")
    @Operation(summary = "Получить пользователей аккаунта", description = "Возвращает список пользователей группового" +
            "аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены"),
    })
    public Set<User> getUsersFromAccount(@Parameter(description = "ID пользователя") @RequestParam Long userId) throws InstanceNotFoundException {
        return accountService.getUsersFromGroupAccount(userId);
    }
}
