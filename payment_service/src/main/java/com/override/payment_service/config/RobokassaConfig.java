package com.override.payment_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "robokassa")
@Data
@Component
public class RobokassaConfig {
    private String loginShopId;
    private String passwordOne;
    private String passwordTwo;
    private String testPasswordOne;
    private String testPasswordTwo;
    private boolean testMode;
    private String baseUrl;
    private String resultUrl;
    private String successUrl;
    private String failUrl;
    private Integer subscriptionAmount;
}
