package com.override.invest_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.invest_service.dto.MarketTQBRDataDTO;
import com.override.invest_service.model.MarketTQBRData;
import com.override.invest_service.model.TinkoffShareInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TinkoffService {
    private final double TOTAL_WEIGHT = 100d;

    @Autowired
    private MOEXService moexService;

    @SneakyThrows
    public List<TinkoffActiveDTO> getActives(String token, String tinkoffAccountId) {
        InvestApi api = InvestApi.createReadonly(token);

        try {
            Portfolio portfolio = api.getOperationsService().getPortfolioSync(tinkoffAccountId);
            List<Position> positions = portfolio.getPositions();

            return positions.stream()
                    .map(position -> {
                        Instrument instrument = api.getInstrumentsService().getInstrumentByFigiSync(
                                position.getFigi()); //todo OV-236 тут надо подумать в какую сторону оптимизировать, пока нет однозначного решения
                        return TinkoffActiveDTO.builder()
                                .name(instrument.getName())
                                .ticker(instrument.getTicker())
                                .figi(position.getFigi())
                                .quantity(position.getQuantity().intValue())
                                .quantityLots(position.getQuantityLots().intValue())
                                .currentPrice(position.getCurrentPrice().getValue().setScale(2, RoundingMode.HALF_UP))
                                .averagePositionPrice(
                                        position.getAveragePositionPrice().getValue().setScale(2, RoundingMode.HALF_UP))
                                .expectedYield(position.getExpectedYield().setScale(2, RoundingMode.HALF_UP))
                                .build();
                    })
                    .collect(Collectors.toList());
        } finally {
            api.destroy(0);
        }
    }

    public List<TinkoffActiveMOEXDTO> getActivesWithMOEXWeight(String token, String tinkoffAccountId,
                                                               Double userTargetInvestAmount) {
        InvestApi api = InvestApi.createReadonly(token);

        Map<String, MarketTQBRDataDTO> marketTQBRDataDTOMap = buildMarketTQBRDataDTO(token, tinkoffAccountId);

        try {
            Map<String, Double> tickerToWeight =
                    rebalanceIndexByAmount(moexService.getTickerToWeight(), marketTQBRDataDTOMap,
                            userTargetInvestAmount);

            Map<String, TinkoffActiveDTO> actives = getActives(token, tinkoffAccountId).stream()
                    .collect(Collectors.toMap(TinkoffActiveDTO::getTicker, Function.identity(), (prev, next) -> next,
                            HashMap::new));

            return tickerToWeight
                    .entrySet()
                    .stream()
                    .map(tickerWeightPair -> {
                        TinkoffActiveDTO active = actives.get(tickerWeightPair.getKey());
                        MarketTQBRDataDTO marketTQBRDataDTO = marketTQBRDataDTOMap.get(tickerWeightPair.getKey());

                        if (marketTQBRDataDTO == null) {
                            log.info("Рыночные данные для: " + tickerWeightPair.getKey() + " отсутствуют.");
                            return TinkoffActiveMOEXDTO.builder()
                                    .tinkoffActiveDTO(
                                            TinkoffActiveDTO.builder().ticker(tickerWeightPair.getKey()).build())
                                    .moexWeight(tickerWeightPair.getValue())
                                    .currentWeight(0d)
                                    .percentFollowage(0d)
                                    .currentTotalPrice(0d)
                                    .correctQuantity(0)
                                    .quantityToBuy(0)
                                    .lot(0)
                                    .build();
                        }

                        if (active == null) {
                            double priceForOne = marketTQBRDataDTO.getPrice();
                            double correctPrice = userTargetInvestAmount * tickerWeightPair.getValue() / TOTAL_WEIGHT;
                            int correctQuantity = (int) (correctPrice / priceForOne);
                            int lots = marketTQBRDataDTO.getLots();
                            if (lots > 1) {
                                correctQuantity = correctQuantity - (correctQuantity % lots);
                            }
                            return TinkoffActiveMOEXDTO.builder()
                                    .tinkoffActiveDTO(
                                            TinkoffActiveDTO.builder().ticker(tickerWeightPair.getKey()).build())
                                    .moexWeight(tickerWeightPair.getValue())
                                    .currentWeight(0d)
                                    .percentFollowage(0d)
                                    .currentTotalPrice(priceForOne * correctQuantity)
                                    .correctQuantity(correctQuantity)//todo
                                    .quantityToBuy(correctQuantity)//todo same
                                    .lot(lots)
                                    .build();
                        }
                        Double currentTotalPrice = active.getQuantity() * active.getCurrentPrice().doubleValue();
                        Double currentWeight = currentTotalPrice / userTargetInvestAmount * TOTAL_WEIGHT;
                        int lots = active.getQuantity() / active.getQuantityLots();

                        double correctPriceToBuy = userTargetInvestAmount * tickerWeightPair.getValue() / TOTAL_WEIGHT;
                        int correctQuantity = (int) (correctPriceToBuy / active.getCurrentPrice().doubleValue());
                        if (lots > 1) {
                            correctQuantity = correctQuantity - (correctQuantity % lots);
                        }
                        return TinkoffActiveMOEXDTO.builder()
                                .currentTotalPrice(roundToTwoPlaces(currentTotalPrice))
                                .tinkoffActiveDTO(active)
                                .moexWeight(roundToTwoPlaces(tickerWeightPair.getValue()))
                                .currentWeight(roundToTwoPlaces(currentWeight))
                                .percentFollowage(
                                        roundToTwoPlaces(TOTAL_WEIGHT * active.getQuantity() / correctQuantity))
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

    private Map<String, MarketTQBRDataDTO> buildMarketTQBRDataDTO(String token, String tinkoffAccountId) {
        MarketTQBRData marketTQBRData = new MarketTQBRData(token, tinkoffAccountId);
        Map<String, Quotation> figiPriceMap = marketTQBRData.getFigiPriceMap();

        return marketTQBRData.getTickerShareMap()
                .entrySet()
                .stream()
                .map(ticker -> {
                    Quotation price = figiPriceMap.get(ticker.getValue().getFigi());

                    return MarketTQBRDataDTO.builder()
                            .ticker(ticker.getKey())
                            .lots(ticker.getValue().getLot())
                            .price(price.getUnits() + price.getNano() / 1_000_000_000d)
                            .build();
                })
                .collect(Collectors.toMap(MarketTQBRDataDTO::getTicker, Function.identity()));
    }

    public Map<String, Double> rebalanceIndexByAmount(Map<String, Double> srcIndex, Map<String,
            MarketTQBRDataDTO> marketTQBRData, double userTargetInvestAmount) {

        Map<String, Double> rebalancedIndex = new HashMap<>();

        double remainderPriceRatioThreshold = 0.01d;
        double overweight = 0d;

        for (var tickerWeightPair : srcIndex.entrySet()) {
            MarketTQBRDataDTO marketTQBRDataDTO = marketTQBRData.get(tickerWeightPair.getKey());

            if (marketTQBRDataDTO == null) {
                log.info("Рыночные данные для: " + tickerWeightPair.getKey() + " отсутствуют.");
                rebalancedIndex.put(tickerWeightPair.getKey(), 0.0d);
                overweight += tickerWeightPair.getValue();
                continue;
            }
            int lots = marketTQBRDataDTO.getLots();
            double priceForOne = marketTQBRDataDTO.getPrice();
            double finalPrice = lots * priceForOne;
            double instrumentCount = (userTargetInvestAmount * tickerWeightPair.getValue() / TOTAL_WEIGHT) / finalPrice;
            double remainderCount = (instrumentCount % 1);
            double remainderPrice = remainderCount * finalPrice;
            double remainderPriceRatio = remainderPrice / userTargetInvestAmount;

            if (instrumentCount < 1.0d || (remainderPriceRatio > remainderPriceRatioThreshold)) {
                rebalancedIndex.put(tickerWeightPair.getKey(), 0.0d);
                overweight += tickerWeightPair.getValue();
            } else {
                rebalancedIndex.put(tickerWeightPair.getKey(), tickerWeightPair.getValue());
            }
        }

        return weightsReallocationByRatio(rebalancedIndex, overweight);
    }

    private Map<String, Double> weightsReallocationByRatio(Map<String, Double> rebalancedIndex,
                                                           Double additionalWeight) {
        for (var tickerWeightPair : rebalancedIndex.entrySet()) {
            double oldWeight = tickerWeightPair.getValue();
            double newWeight = oldWeight +
                    (oldWeight / (TOTAL_WEIGHT - additionalWeight) * additionalWeight);

            rebalancedIndex.put(tickerWeightPair.getKey(), newWeight);
        }

        return rebalancedIndex;
    }

    private Double calculateMinimalPorfolioSum(MarketTQBRData marketTQBRData) {
        TinkoffShareInfo shareHighestPriceForLot = getMOEXSharesInfo(marketTQBRData).stream()
                .max(Comparator.comparing(TinkoffShareInfo::getPriceForLot))
                .get();

        Double weightOfHighestShare = moexService.getTickerToWeight().get(shareHighestPriceForLot.getTicker());
        return shareHighestPriceForLot.getPriceForLot() * TOTAL_WEIGHT / weightOfHighestShare;
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
                    .filter(account -> account.getStatus() != ACCOUNT_STATUS_CLOSED &&
                            account.getStatus() != UNRECOGNIZED)
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

