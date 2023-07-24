package com.override.invest_service.controller.rest;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.invest_service.service.TinkoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

;

@RestController
@RequestMapping("/tinkoff")
public class TinkoffRestController {

    @Autowired
    private TinkoffService tinkoffService;

    @GetMapping("/actives")
    public List<TinkoffActiveDTO> getActives(@RequestParam("token") String token,
                                             @RequestParam("accountId") String accountId) {
        return tinkoffService.getActives(token, accountId);
    }

    @GetMapping("/moex")
    public List<TinkoffActiveMOEXDTO> getActivesMoexPercentage(@RequestParam("token") String token,
                                                               @RequestParam("accountId") String accountId) {
        return tinkoffService.getActivesWithMOEXWeight(token, accountId);
    }

    /**
     * @return Список брокерских счетов пользователя
     */
    @GetMapping("/accounts")
    public List<TinkoffAccountDTO> getUserAccounts(@RequestParam("token") String token) {
        return tinkoffService.getAccounts(token);
    }
}
