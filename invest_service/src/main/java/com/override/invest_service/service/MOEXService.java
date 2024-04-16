package com.override.invest_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.override.invest_service.feign.MOEXFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MOEXService {

    private final String IMOEX_DATA_FILENAME = "IMOEX_Data-invest.json";
    private final File jsonDataFile = new File(IMOEX_DATA_FILENAME);
    private final Long DATA_AGE_LIMIT = 1L;
    private final int SECIDS_INDEX = 4;
    private final int WEIGHT_INDEX = 5;

    @Autowired
    private MOEXFeignClient MOEXFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectWriter objectWriter;

    private Map<String, Double> tickerToWeight = new HashMap<>();


    @PostConstruct
    public void init() {
        try {
            JsonNode jsonNode;

            if (jsonDataFile.exists() && checkJsonByFreshness(jsonDataFile)) {
                jsonNode = readDataFromFile(jsonDataFile);
            } else {
                ResponseEntity<JsonNode> response = MOEXFeignClient.getIndexIMOEX();

                if (response.getStatusCode() == HttpStatus.OK) {
                    jsonNode = response.getBody();
                    objectWriter.writeValue(jsonDataFile, jsonNode);
                } else {
                    jsonNode = readDataFromFile(jsonDataFile);
                }
            }

            for (JsonNode jn : jsonNode.get("analytics").get("data")) {
                tickerToWeight.put(jn.get(SECIDS_INDEX).asText(), jn.get(WEIGHT_INDEX).asDouble());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }

    private boolean checkJsonByFreshness(File file) {
        return (LocalDate.now().toEpochDay() - TimeUnit.MILLISECONDS.toDays(file.lastModified()) <= DATA_AGE_LIMIT);
    }

    private JsonNode readDataFromFile(File jsonDataFile) {
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(jsonDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonNode;
    }
}
