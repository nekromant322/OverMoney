package com.override.orchestrator_service.controller.rest;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.service.InvestTinkoffInfoService;
import com.override.orchestrator_service.service.OverMoneyAccountService;
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
    private OverMoneyAccountService overMoneyAccountService;

    @Autowired
    private TelegramUtils telegramUtils;

    @GetMapping
    public TinkoffInfoDTO getUserInfo(Principal principal) {
        Long overMoneyAccountId = overMoneyAccountService
                .getOverMoneyAccountByChatId(telegramUtils.getTelegramId(principal)).getId();
        return investTinkoffInfoService.findTinkoffInfo(overMoneyAccountId);
    }

    @PostMapping
    public void saveTinkoffToken(Principal principal, @RequestBody TinkoffInfoDTO tinkoffInfoDTO) {
        Long overMoneyAccountId = overMoneyAccountService
                .getOverMoneyAccountByChatId(telegramUtils.getTelegramId(principal)).getId();
        tinkoffInfoDTO.setTinkoffAccountId(overMoneyAccountId);
        investTinkoffInfoService.saveTinkoffinfo(tinkoffInfoDTO);
    }

    @GetMapping("/accounts")
    public List<TinkoffAccountDTO> getUserAccounts(@RequestParam("token") String token) {
        return investTinkoffInfoService.getUserAccounts(token);
    }

    @GetMapping("/moex")
    public List<TinkoffActiveMOEXDTO> getActivesMoexPercentage(@RequestParam("token") String token,
                                                               @RequestParam("tinkoffAccountId") String tinkoffAccountId) {
        return investTinkoffInfoService.getActivesMoexPercentage(token, tinkoffAccountId);
    }
}
