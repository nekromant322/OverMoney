package com.override.invest_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.override.dto.IMOEXDataDTO;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MOEXService {

    private final String IMOEX_DATA_FILENAME = "IMOEX_Data-invest.json";
    private final File jsonDataFile = new File(IMOEX_DATA_FILENAME);
    private final Long DATA_AGE_LIMIT = 1L;

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
            Optional<IMOEXDataDTO> imoexDataDTO;

            if (jsonDataFile.exists() && checkJsonByFreshness(jsonDataFile)) {
                imoexDataDTO = Optional.of(readDataFromFile(jsonDataFile));
            } else {
                ResponseEntity<IMOEXDataDTO> response = MOEXFeignClient.getIndexIMOEX();

                if (response.getStatusCode() == HttpStatus.OK) {
                    imoexDataDTO = Optional.ofNullable(response.getBody());
                    objectWriter.writeValue(jsonDataFile, imoexDataDTO);
                } else {
                    imoexDataDTO = Optional.of(readDataFromFile(jsonDataFile));
                }
            }

            tickerToWeight = imoexDataDTO
                    .orElseThrow()
                    .getAnalytics()
                    .getImoexData()
                    .stream()
                    .collect(Collectors.toMap(
                            IMOEXDataDTO.IMOEXData::getSecIds,
                            IMOEXDataDTO.IMOEXData::getWeight));

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

    private IMOEXDataDTO readDataFromFile(File jsonDataFile) {
        try {
            return objectMapper.readValue(jsonDataFile, IMOEXDataDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
