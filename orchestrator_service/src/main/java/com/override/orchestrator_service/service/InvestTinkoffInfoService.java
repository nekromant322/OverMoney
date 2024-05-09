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

    private final Double DEFAULT_INVEST_AMOUNT = 30_000_000d;

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
                .investAmount(info.getInvestAmount())
                .build()).orElse(null);
    }

    public List<TinkoffAccountDTO> getUserAccounts(String token) {
        Optional<String> optionalToken = Optional.ofNullable(token);
        if (optionalToken.isPresent() && !StringUtils.isEmpty(token)) {
            return investFeign.getUserAccounts(optionalToken.get());
        }
        return null;
    }

    public List<TinkoffActiveMOEXDTO> getActivesMoexPercentage(String token, String tinkoffAccountId, Double investAmount) {
        Optional<String> optionalToken = Optional.ofNullable(token);
        Optional<String> optionalTinkoffAccountId = Optional.ofNullable(tinkoffAccountId);
        Optional<Double> optionalInvestAmount = Optional.ofNullable(investAmount);

        if (optionalToken.isPresent() && !StringUtils.isEmpty(token) && optionalTinkoffAccountId.isPresent()) {
            List<TinkoffActiveMOEXDTO> activesMoexPercentage =
                    investFeign.getActivesMoexPercentage(
                                    optionalToken.get(),
                                    optionalTinkoffAccountId.get(),
                                    optionalInvestAmount.orElse(getInvestAmountByToken(token)))
                            .stream()
                            .peek(active -> {
                                active.getTinkoffActiveDTO().setCurrentPrice(NumericalUtils.roundAmount(active.getTinkoffActiveDTO().getCurrentPrice()));
                                active.getTinkoffActiveDTO().setAveragePositionPrice(NumericalUtils.roundAmount(active.getTinkoffActiveDTO().getAveragePositionPrice()));
                                active.setCurrentTotalPrice(NumericalUtils.roundAmount(active.getCurrentTotalPrice()));
                                active.setMoexWeight(NumericalUtils.roundAmount(active.getMoexWeight()));
                                active.setCurrentWeight(NumericalUtils.roundAmount(active.getCurrentWeight()));
                                active.setPercentFollowage(NumericalUtils.roundAmount(active.getPercentFollowage()));
                            })
                            .collect(Collectors.toList());

            optionalInvestAmount.ifPresent(aDouble -> updateInvestAmount(optionalToken.get(), aDouble));

            return activesMoexPercentage;
        }
        return null;
    }

    @SneakyThrows
    public void saveTinkoffinfo(TinkoffInfoDTO tinkoffInfoDTO) {
        Optional<TinkoffInfoDTO> infoDTO = findTinkoffInfoDTO(tinkoffInfoDTO.getTinkoffAccountId());
        if (infoDTO.isEmpty() || !Objects.equals(infoDTO.get(), tinkoffInfoDTO)) {
            OverMoneyAccount account = overMoneyAccountService.getOverMoneyAccountById(tinkoffInfoDTO.getTinkoffAccountId());
            Optional<Long> favoriteAccountId = Optional.ofNullable(tinkoffInfoDTO.getFavoriteAccountId());
            Double investAmount = Optional.ofNullable(tinkoffInfoDTO.getInvestAmount()).orElseGet(() -> {
                if (infoDTO.isEmpty()) {
                    return null;
                } else {
                    return infoDTO.get().getInvestAmount();
                }
            });

            TinkoffInfo tinkoffInfo = TinkoffInfo
                    .builder()
                    .id(tinkoffInfoDTO.getTinkoffAccountId())
                    .token(tinkoffInfoDTO.getToken())
                    .favoriteAccountId(favoriteAccountId.orElse(null))
                    .investAmount(investAmount)
                    .account(account)
                    .build();
            investTinkoffInfoRepository.save(tinkoffInfo);
        }
    }

    @SneakyThrows
    public void updateInvestAmount(String token, Double investAmount) {
        Optional<TinkoffInfoDTO> optionalTinkoffInfoDTO = findTinkoffInfoDTOByToken(token);
        System.out.println(optionalTinkoffInfoDTO);

        if (optionalTinkoffInfoDTO.isPresent()) {
            optionalTinkoffInfoDTO.get().setInvestAmount(investAmount);
            saveTinkoffinfo(optionalTinkoffInfoDTO.get());
        }
    }

    private double getInvestAmountByToken(String token) {
        Optional<TinkoffInfoDTO> tinkoffInfoDTOByToken = findTinkoffInfoDTOByToken(token);
        return tinkoffInfoDTOByToken.map(TinkoffInfoDTO::getInvestAmount)
                .orElse(DEFAULT_INVEST_AMOUNT);
    }

    private Optional<TinkoffInfoDTO> findTinkoffInfoDTO(Long overMoneyAccountId) {
        Optional<TinkoffInfo> tinkoffInfo = investTinkoffInfoRepository.findTinkoffInfoById(overMoneyAccountId);

        return convertTinkoffInfoToDTO(tinkoffInfo);
    }

    private Optional<TinkoffInfoDTO> findTinkoffInfoDTOByToken(String token) {
        Optional<TinkoffInfo> tinkoffInfo = investTinkoffInfoRepository.findByToken(token);

        return convertTinkoffInfoToDTO(tinkoffInfo);
    }

    private Optional<TinkoffInfoDTO> convertTinkoffInfoToDTO(Optional<TinkoffInfo> tinkoffInfo) {
        return tinkoffInfo.map(info -> TinkoffInfoDTO.builder()
                .tinkoffAccountId(info.getId())
                .token(info.getToken())
                .favoriteAccountId(info.getFavoriteAccountId())
                .investAmount(info.getInvestAmount())
                .build());
    }
}
