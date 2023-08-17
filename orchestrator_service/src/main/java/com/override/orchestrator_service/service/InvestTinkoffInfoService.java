package com.override.orchestrator_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.feign.InvestFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.TinkoffInfo;
import com.override.orchestrator_service.repository.InvestTinkoffInfoRepository;
import com.override.orchestrator_service.util.NumericalUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<String> optionalToken = Optional.ofNullable(token);
        if (optionalToken.isPresent() && !StringUtils.isEmpty(token)) {
            return investFeign.getUserAccounts(optionalToken.get());
        }
        return null;
    }

    public List<TinkoffActiveMOEXDTO> getActivesMoexPercentage(String token, String tinkoffAccountId) {
        Optional<String> optionalToken = Optional.ofNullable(token);
        Optional<String> optionalTinkoffAccountId = Optional.ofNullable(tinkoffAccountId);

        if (optionalToken.isPresent() && !StringUtils.isEmpty(token) && optionalTinkoffAccountId.isPresent()) {
            return investFeign.getActivesMoexPercentage(optionalToken.get(), optionalTinkoffAccountId.get()).stream()
                    .peek(active -> {
                        active.getTinkoffActiveDTO().setCurrentPrice(NumericalUtils.roundAmount(active.getTinkoffActiveDTO().getCurrentPrice()));
                        active.getTinkoffActiveDTO().setAveragePositionPrice(NumericalUtils.roundAmount(active.getTinkoffActiveDTO().getAveragePositionPrice()));
                        active.setCurrentTotalPrice(NumericalUtils.roundAmount(active.getCurrentTotalPrice()));
                        active.setMoexWeight(NumericalUtils.roundAmount(active.getMoexWeight()));
                        active.setCurrentWeight(NumericalUtils.roundAmount(active.getCurrentWeight()));
                        active.setPercentFollowage(NumericalUtils.roundAmount(active.getPercentFollowage()));
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    @SneakyThrows
    public void saveTinkoffinfo(TinkoffInfoDTO tinkoffInfoDTO) {
        Optional<TinkoffInfoDTO> infoDTO = findTinkoffInfoDTO(tinkoffInfoDTO.getTinkoffAccountId());
        if (infoDTO.isEmpty() || !Objects.equals(infoDTO.get(), tinkoffInfoDTO)) {
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

    private Optional<TinkoffInfoDTO> findTinkoffInfoDTO(Long overMoneyAccountId) {
        Optional<TinkoffInfo> tinkoffInfo = investTinkoffInfoRepository.findTinkoffInfoById(overMoneyAccountId);
        return tinkoffInfo.map(info -> TinkoffInfoDTO.builder()
                .tinkoffAccountId(info.getId())
                .token(info.getToken())
                .favoriteAccountId(info.getFavoriteAccountId())
                .build());
    }
}
