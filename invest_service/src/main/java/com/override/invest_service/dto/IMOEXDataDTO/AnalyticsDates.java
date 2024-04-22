package com.override.invest_service.dto.IMOEXDataDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, value = "date")
public class AnalyticsDates {
    private final int DATES_DATA_INDEX = 0;
    private final int ACTUAL_DATE_INDEX = 1;

    @JsonProperty("data")
    private final ArrayList<ArrayList<String>> rawData;
    private final LocalDate date;

    public AnalyticsDates(@JsonProperty("data") ArrayList<ArrayList<String>> rawData) {
        this.rawData = rawData;
        this.date = LocalDate.parse(rawData.get(DATES_DATA_INDEX).get(ACTUAL_DATE_INDEX));
    }
}
