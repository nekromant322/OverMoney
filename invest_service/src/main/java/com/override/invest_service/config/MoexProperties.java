package com.override.invest_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "moex")
@Getter
@Setter
public class MoexProperties {
    public static final String IMOEX_DATA_FILENAME = "classpath:moexReserveData/imoex.json";

    private String indexUrlData;
}
