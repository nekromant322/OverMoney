package com.override.orchestrator_service.controller.rest;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.service.InvestTinkoffInfoService;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tinkoff")
public class InvestTinkoffInfoController {

    @Autowired
    private InvestTinkoffInfoService investTinkoffInfoService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping()
    public TinkoffInfoDTO getUserInfo(Principal principal) {
        Long userId = telegramUtils.getTelegramId(principal);
        return investTinkoffInfoService.findTinkoffInfo(userId);
    }

    @PostMapping()
    public void saveTinkoffToken(Principal principal, @RequestBody String token) {
        Long userId = telegramUtils.getTelegramId(principal);
        investTinkoffInfoService.saveTinkoffToken(userId, token);
    }

    @GetMapping("/accounts")
    public List<TinkoffAccountDTO> getUserAccounts(@RequestParam("token") String token) {
        return investTinkoffInfoService.getUserAccounts(token);
    }

    @PutMapping()
    public void updateTinkoffInfo(Principal principal,
                                  @PathVariable("token") String token,
                                  @PathVariable("favoriteAccountId") Long favoriteAccountId) {
        Long userId = telegramUtils.getTelegramId(principal);
        investTinkoffInfoService.updateTinkoffInfo(userId, token, favoriteAccountId);
    }
}
