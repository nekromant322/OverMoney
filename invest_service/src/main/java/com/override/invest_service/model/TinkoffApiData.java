package com.override.invest_service.model;

import lombok.Builder;
import lombok.Data;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TinkoffApiData {
    private final String MOEX_CLASS_CODE = "TQBR";
    private final String tinkoffAccountId;
    private final String token;
    private final Map<String, Share> tickerShareMap;
    private final Map<String, Quotation> figiPriceMap;

    public TinkoffApiData(String token, String tinkoffAccountId) {
        this.tinkoffAccountId = tinkoffAccountId;
        this.token = token;

        InvestApi api = InvestApi.createReadonly(token);

        try {
            this.tickerShareMap = buildSharesMap(api);
            this.figiPriceMap = buildFigiesPricesMap(api);
        } finally {
            api.destroy(0);
        }
    }

    private Map<String, Share> buildSharesMap(InvestApi api) {
        List<Share> shares = api.getInstrumentsService().getTradableSharesSync().stream()
                .filter(s -> s.getClassCode().equals(MOEX_CLASS_CODE))
                .collect(Collectors.toList());

        return shares.stream()
                .collect(Collectors.toMap(Share::getTicker, Function.identity()));
    }

    private Map<String, Quotation> buildFigiesPricesMap(InvestApi api) {
        List<String> figies = tickerShareMap
                .values()
                .stream()
                .map(Share::getFigi)
                .collect(Collectors.toList());

        return api.getMarketDataService().getLastPricesSync(figies).stream()
                .collect(Collectors.toMap(LastPrice::getFigi, LastPrice::getPrice));
    }
}

