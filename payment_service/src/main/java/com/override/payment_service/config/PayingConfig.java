package com.override.payment_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "paying")
@Data
@Component
public class PayingConfig {
    private BigDecimal subscriptionAmount;
}
