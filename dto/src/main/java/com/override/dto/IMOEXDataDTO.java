package com.override.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.override.dto.constants.IMOEXDataIndeces;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IMOEXDataDTO {
    private Analytics analytics;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true, value = "imoexData")
    public static class Analytics {
        @JsonProperty("data")
        private final ArrayList<ArrayList<String>> rawData;
        private final List<IMOEXData> imoexData;

        public Analytics(@JsonProperty("data") ArrayList<ArrayList<String>> rawData) {
            this.imoexData = rawData.stream()
                    .map(IMOEXData::new)
                    .collect(Collectors.toList());

            this.rawData = rawData;
        }
    }

    @Data
    public static class IMOEXData {
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
}
