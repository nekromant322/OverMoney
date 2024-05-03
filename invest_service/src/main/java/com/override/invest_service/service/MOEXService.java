package com.override.invest_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.invest_service.dto.IMOEXDataDTO.IMOEXData;
import com.override.invest_service.dto.IMOEXDataDTO.IMOEXDataDTO;
import com.override.invest_service.feign.MOEXFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MOEXService {
    public static final String IMOEX_DATA_FILENAME = "classpath:moexReserveData/imoex.json";
    private final int DATA_AGE_LIMIT = 1;

    private Optional<IMOEXDataDTO> cachedImoexDataDTO = Optional.empty();
    private Map<String, Double> tickerToWeight = new HashMap<>();

    @Autowired
    private MOEXFeignClient MOEXFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        updateIMOEXData();
    }

    private void updateIMOEXData() {
        Optional<IMOEXDataDTO> imoexDataDTO;

        if (cachedImoexDataDTO.isPresent() && checkCachedDataByFreshness()) {
            imoexDataDTO = cachedImoexDataDTO;
        } else {
            ResponseEntity<IMOEXDataDTO> response = MOEXFeignClient.getIndexIMOEX();

            if (response.getStatusCode() == HttpStatus.OK) {
                imoexDataDTO = Optional.ofNullable(response.getBody());
            } else {
                imoexDataDTO = Optional.ofNullable(getReserveData());
            }

            cachedImoexDataDTO = imoexDataDTO;
        }

        tickerToWeight = imoexDataDTO
                .orElseThrow()
                .getAnalytics()
                .getImoexData()
                .stream()
                .collect(Collectors.toMap(
                        IMOEXData::getSecIds,
                        IMOEXData::getWeight));
    }

    public Map<String, Double> getTickerToWeight() {
        updateIMOEXData();
        return tickerToWeight;
    }

    private IMOEXDataDTO getReserveData() {
        try {
            if (cachedImoexDataDTO.isPresent()) {
                return cachedImoexDataDTO.get();
            } else {
                return objectMapper.readValue(
                        ResourceUtils.getFile(IMOEX_DATA_FILENAME), IMOEXDataDTO.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkCachedDataByFreshness() {
        long dateAge = LocalDate.now().toEpochDay()
                - cachedImoexDataDTO.get().getAnalyticsDates().getDate().toEpochDay();

        return (dateAge <= DATA_AGE_LIMIT);
    }
}
