package com.override.invest_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.invest_service.model.MarketTQBRData;
import com.override.invest_service.model.TinkoffShareInfo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.AccountStatus.ACCOUNT_STATUS_CLOSED;
import static ru.tinkoff.piapi.contract.v1.AccountStatus.UNRECOGNIZED;

@Service
public class TinkoffService {

    @Autowired
    private MOEXService moexService;

    @SneakyThrows
    public List<TinkoffActiveDTO> getActives(String token, String tinkoffAccountId) {
        return getActives(new MarketTQBRData(token, tinkoffAccountId));
    }

    @SneakyThrows
    public List<TinkoffActiveDTO> getActives(MarketTQBRData marketTQBRData) {
        InvestApi api = InvestApi.createReadonly(marketTQBRData.getToken());

        try {
            Portfolio portfolio = api.getOperationsService().getPortfolioSync(marketTQBRData.getTinkoffAccountId());
            List<Position> positions = portfolio.getPositions();

            return positions.stream()
                    .map(position -> {
                        Instrument instrument = api.getInstrumentsService().getInstrumentByFigiSync(position.getFigi()); //todo OV-236 тут надо подумать в какую сторону оптимизировать, пока нет однозначного решения
                        return TinkoffActiveDTO.builder()
                                .name(instrument.getName())
                                .ticker(instrument.getTicker())
                                .figi(position.getFigi())
                                .quantity(position.getQuantity().intValue())
                                .quantityLots(position.getQuantityLots().intValue())
                                .currentPrice(position.getCurrentPrice().getValue().setScale(2, RoundingMode.HALF_UP))
                                .averagePositionPrice(position.getAveragePositionPrice().getValue().setScale(2, RoundingMode.HALF_UP))
                                .expectedYield(position.getExpectedYield().setScale(2, RoundingMode.HALF_UP))
                                .build();
                    })
                    .collect(Collectors.toList());
        } finally {
            api.destroy(0);
        }
    }

    public List<TinkoffActiveMOEXDTO> getActivesWithMOEXWeight(String token, String tinkoffAccountId) {
        InvestApi api = InvestApi.createReadonly(token);
        MarketTQBRData marketTQBRData = new MarketTQBRData(token, tinkoffAccountId);

        try {
            final Double minimalSum = calculateMinimalPorfolioSum(marketTQBRData);

            Map<String, Double> tickerToWeight = moexService.getTickerToWeight();
            Map<String, TinkoffActiveDTO> actives = getActives(marketTQBRData).stream()
                    .collect(Collectors.toMap(TinkoffActiveDTO::getTicker, Function.identity(), (prev, next) -> next, HashMap::new));

            Map<String, Share> tickerShareMap = marketTQBRData.getTickerShareMap();
            Map<String, Quotation> figiPriceMap = marketTQBRData.getFigiPriceMap();

            return tickerToWeight
                    .entrySet()
                    .stream()
                    .map(pair -> {
                        TinkoffActiveDTO active = actives.get(pair.getKey());
                        if (active == null) {
                            Share share = tickerShareMap.get(pair.getKey());
                            Quotation price = figiPriceMap.get(share.getFigi());
                            int lots = share.getLot();
                            Double priceForOne = price.getUnits() + price.getNano() / 1_000_000_000d;
                            Double correctPrice = minimalSum * pair.getValue() / 100;
                            int correctQuantity = (int) (correctPrice / priceForOne);
                            if (lots > 1) {
                                correctQuantity = correctQuantity - (correctQuantity % lots);
                            }
                            return TinkoffActiveMOEXDTO.builder()
                                    .tinkoffActiveDTO(TinkoffActiveDTO.builder().ticker(pair.getKey()).build())
                                    .moexWeight(pair.getValue() / 100)
                                    .currentWeight(0d)
                                    .percentFollowage(0d)
                                    .correctQuantity(correctQuantity)//todo
                                    .quantityToBuy(correctQuantity)//todo same
                                    .lot(lots)
                                    .build();
                        }
                        Double currentTotalPrice = active.getQuantity() * active.getCurrentPrice().doubleValue();
                        Double currentWeight = currentTotalPrice / minimalSum * 100;
                        int lots = active.getQuantity() / active.getQuantityLots();

                        double correctPriceToBuy = minimalSum * pair.getValue() / 100d;
                        int correctQuantity = (int) (correctPriceToBuy / active.getCurrentPrice().doubleValue());
                        if (lots > 1) {
                            correctQuantity = correctQuantity - (correctQuantity % lots);
                        }
                        return TinkoffActiveMOEXDTO.builder()
                                .currentTotalPrice(roundToTwoPlaces(currentTotalPrice))
                                .tinkoffActiveDTO(active)
                                .moexWeight(roundToTwoPlaces(pair.getValue()))
                                .currentWeight(roundToTwoPlaces(currentWeight))
                                .percentFollowage(roundToTwoPlaces(100d * active.getQuantity() / correctQuantity))
                                .correctQuantity(correctQuantity)
                                .quantityToBuy(correctQuantity - active.getQuantity())
                                .lot(lots)
                                .build();
                    })
                    .sorted((Comparator.comparing(TinkoffActiveMOEXDTO::getPercentFollowage).reversed()))
                    .collect(Collectors.toList());
        } finally {
            api.destroy(0);
        }
    }

    private Double calculateMinimalPorfolioSum(MarketTQBRData marketTQBRData) {
        TinkoffShareInfo shareHighestPriceForLot = getMOEXSharesInfo(marketTQBRData).stream()
                .max(Comparator.comparing(TinkoffShareInfo::getPriceForLot))
                .get();

        Double weightOfHighestShare = moexService.getTickerToWeight().get(shareHighestPriceForLot.getTicker());
        return shareHighestPriceForLot.getPriceForLot() * 100 / weightOfHighestShare;
    }

    private List<TinkoffShareInfo> getMOEXSharesInfo(MarketTQBRData marketTQBRData) {
        Map<String, Quotation> figiPriceMap = marketTQBRData.getFigiPriceMap();

        return moexService.getTickerToWeight()
                .keySet()
                .stream()
                .map(marketTQBRData.getTickerShareMap()::get)
                .map(share -> {
                            Quotation price = figiPriceMap.get(share.getFigi());
                            return TinkoffShareInfo.builder()
                                    .name(share.getName())
                                    .ticker(share.getTicker())
                                    .lot(share.getLot())
                                    .priceForOne(price.getUnits() + price.getNano() / 1_000_000_000d)
                                    .priceForLot((price.getUnits() + price.getNano() / 1_000_000_000d) * share.getLot())
                                    .build();
                        }
                ).collect(Collectors.toList());
    }

    public List<TinkoffAccountDTO> getAccounts(String token) {
        InvestApi api = InvestApi.createReadonly(token);

        try {
            return api.getUserService().getAccountsSync().stream()
                    .filter(account -> account.getStatus() != ACCOUNT_STATUS_CLOSED && account.getStatus() != UNRECOGNIZED)
                    .map(account -> TinkoffAccountDTO.builder()
                            .investAccountId(account.getId())
                            .investAccountName(account.getName())
                            .build())
                    .collect(Collectors.toList());
        } finally {
            api.destroy(0);
        }
    }

    private Double roundToTwoPlaces(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

