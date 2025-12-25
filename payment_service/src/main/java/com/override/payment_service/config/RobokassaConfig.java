package com.override.payment_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "robokassa")
@Data
public class RobokassaConfig {
    private String login;
    private String password1;
    private String password2;
    private String testPassword1;
    private String testPassword2;
    private boolean testMode;
    private String baseUrl;
    private String resultUrl;
    private String successUrl;
    private String failUrl;
}
