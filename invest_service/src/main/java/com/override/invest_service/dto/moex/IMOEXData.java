package com.override.invest_service.dto.moex;

import com.override.invest_service.dto.constants.IMOEXDataIndeces;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
public class IMOEXData {
    private final String indexId;
    private final LocalDate tradeDate;
    private final String ticker;
    private final String shortNames;
    private final String secIds;
    private final double weight;
    private final int tradingSession;

    public IMOEXData(ArrayList<String> data) {
        this.indexId = data.get(IMOEXDataIndeces.INDEXID_INDEX);
        this.tradeDate = LocalDate.parse(data.get(IMOEXDataIndeces.TRADEDATE_INDEX));
        this.ticker = data.get(IMOEXDataIndeces.TICKER_INDEX);
        this.shortNames = data.get(IMOEXDataIndeces.SHORTNAMES_INDEX);
        this.secIds = data.get(IMOEXDataIndeces.SECIDS_INDEX);
        this.weight = Double.parseDouble(data.get(IMOEXDataIndeces.WEIGHT_INDEX));
        this.tradingSession = Integer.parseInt(data.get(IMOEXDataIndeces.TRADINGSESSION_INDEX));
    }
}
