package com.override.invest_service.config;

import com.override.dto.constants.Type;
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
    private String indexUrlData;
}
