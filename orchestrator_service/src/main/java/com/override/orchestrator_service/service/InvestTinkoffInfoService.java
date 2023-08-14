package com.override.orchestrator_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.feign.InvestFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.TinkoffInfo;
import com.override.orchestrator_service.repository.InvestTinkoffInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvestTinkoffInfoService {

    @Autowired
    private InvestFeign investFeign;

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    @Autowired
    private InvestTinkoffInfoRepository investTinkoffInfoRepository;

    public TinkoffInfoDTO findTinkoffInfo(Long userId) {
        Optional<TinkoffInfo> tinkoffInfo = investTinkoffInfoRepository.findTinkoffInfoById(userId);
        return tinkoffInfo.map(info -> TinkoffInfoDTO.builder()
                .id(info.getId())
                .token(info.getToken())
                .favoriteAccountId(info.getFavoriteAccountId()) //TODO Вот тут подумать. Не будет ли тут null?
                .build()).orElse(null);
    }

    public void saveTinkoffToken(Long userId, String token) {
        OverMoneyAccount account = overMoneyAccountService.getAccountByUserId(userId);
        if (investTinkoffInfoRepository.findTinkoffInfoById(account.getId()).isEmpty()) {
            TinkoffInfo tinkoffInfo = TinkoffInfo.builder()
                    .token(token)
                    .favoriteAccountId(null)
                    .account(account)
                    .build();
            investTinkoffInfoRepository.save(tinkoffInfo);
        }
    }

    public void updateTinkoffInfo(Long userId, String token, Long favoriteAccountId) {
        OverMoneyAccount account = overMoneyAccountService.getAccountByUserId(userId);
        TinkoffInfo tinkoffInfo = TinkoffInfo
                .builder()
                .token(token)
                .favoriteAccountId(favoriteAccountId)
                .account(account)
                .build();
        investTinkoffInfoRepository.save(tinkoffInfo);
    }

    public List<TinkoffAccountDTO> getUserAccounts(String token) {
        return investFeign.getUserAccounts(token);
    }
}
