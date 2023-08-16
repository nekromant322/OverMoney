package com.override.orchestrator_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.feign.InvestFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.TinkoffInfo;
import com.override.orchestrator_service.repository.InvestTinkoffInfoRepository;
import lombok.SneakyThrows;
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

    public TinkoffInfoDTO findTinkoffInfo(Long overMoneyAccountId) {
        Optional<TinkoffInfo> tinkoffInfo = investTinkoffInfoRepository.findTinkoffInfoById(overMoneyAccountId);
        return tinkoffInfo.map(info -> TinkoffInfoDTO.builder()
                .tinkoffAccountId(info.getId())
                .token(info.getToken())
                .favoriteAccountId(info.getFavoriteAccountId())
                .build()).orElse(null);
    }

    public List<TinkoffAccountDTO> getUserAccounts(String token) {
        return investFeign.getUserAccounts(token);
    }

    public List<TinkoffActiveMOEXDTO> getActivesMoexPercentage(String token, String tinkoffAccountId) {
        return investFeign.getActivesMoexPercentage(token, tinkoffAccountId);
    }

    @SneakyThrows
    public void saveTinkoffinfo(TinkoffInfoDTO tinkoffInfoDTO) {
        OverMoneyAccount account = overMoneyAccountService.getOverMoneyAccountById(tinkoffInfoDTO.getTinkoffAccountId());
        Optional<Long> favoriteAccountId = Optional.ofNullable(tinkoffInfoDTO.getFavoriteAccountId());
        TinkoffInfo tinkoffInfo = TinkoffInfo
                .builder()
                .id(tinkoffInfoDTO.getTinkoffAccountId())
                .token(tinkoffInfoDTO.getToken())
                .favoriteAccountId(favoriteAccountId.orElse(null))
                .account(account)
                .build();
        investTinkoffInfoRepository.save(tinkoffInfo);
    }
}
