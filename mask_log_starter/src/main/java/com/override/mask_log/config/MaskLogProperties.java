package com.override.mask_log.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mask-log-spring-boot-starter")
@Getter
@Setter
public class MaskLogProperties {

    public static final Integer DEFAULT_MASK_PERCENTAGE = 75;
    public static final String DEFAULT_MASK = "*****";

    private Integer maskPercentage = DEFAULT_MASK_PERCENTAGE;
    private String mask = DEFAULT_MASK;
    private List<String> maskedFields;
}