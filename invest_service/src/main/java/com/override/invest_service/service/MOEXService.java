package com.override.invest_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.invest_service.config.MoexProperties;
import com.override.invest_service.dto.moex.IMOEXData;
import com.override.invest_service.dto.moex.IMOEXDataDTO;
import com.override.invest_service.feign.MOEXFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MOEXService {
    private final int DATA_AGE_LIMIT = 1;
    private final Object lock = new Object();

    private Optional<IMOEXDataDTO> cachedImoexDataDTO = Optional.empty();
    private Map<String, Double> tickerToWeight = new ConcurrentHashMap<>();

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

        synchronized (lock) {
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
    }

    public Map<String, Double> getTickerToWeight() {
        updateIMOEXData();
        return new ConcurrentHashMap<>(tickerToWeight);
    }

    private IMOEXDataDTO getReserveData() {
        try {
            if (cachedImoexDataDTO.isPresent()) {
                return cachedImoexDataDTO.get();
            } else {
                return objectMapper.readValue(
                        ResourceUtils.getFile(MoexProperties.IMOEX_DATA_FILENAME), IMOEXDataDTO.class);
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
