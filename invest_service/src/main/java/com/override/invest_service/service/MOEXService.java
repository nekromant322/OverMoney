package com.override.invest_service.service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MOEXService {

    private final String IMOEX_JSON_STRING_URL = "https://iss.moex.com/iss/statistics/engines/stock/markets/index/analytics/imoex.json?limit=100";
    private final String IMOEX_DATA_FILENAME = "IMOEX_Data-invest.json";
    private final Long DATA_AGE_LIMIT = 1L;
    private final int SECIDS_INDEX = 4;
    private final int WEIGHT_INDEX = 5;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
    private Map<String, Double> tickerToWeight = new HashMap<>();


    @PostConstruct
    public void init() {
        try {
            File jsonDataFile = new File(IMOEX_DATA_FILENAME);
            JsonNode jsonNode;

            if (jsonDataFile.exists() && checkJsonByFreshness(jsonDataFile)) {

                jsonNode = objectMapper.readTree(jsonDataFile);
            } else {
                URL imoexJsonURL = new URL(IMOEX_JSON_STRING_URL);
                jsonNode = objectMapper.readTree(imoexJsonURL);
                objectWriter.writeValue(new File(IMOEX_DATA_FILENAME), jsonNode);
            }

            for (JsonNode jn : jsonNode.get("analytics").get("data")) {
                tickerToWeight.put(jn.get(SECIDS_INDEX).asText(), jn.get(WEIGHT_INDEX).asDouble());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }

    private boolean checkJsonByFreshness(File file) {
        return (LocalDate.now().toEpochDay() - TimeUnit.MILLISECONDS.toDays(file.lastModified()) <= DATA_AGE_LIMIT);
    }
}
