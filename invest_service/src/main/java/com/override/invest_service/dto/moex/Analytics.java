package com.override.invest_service.dto.moex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, value = "imoexData")
public class Analytics {

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
