package com.override.payment_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class YooKassaFeignConfig {
    @Value("${yookassa.shop-id}")
    private String shopId;

    @Value("${yookassa.secret-key}")
    private String secretKey;

    @Bean
    public Base64.Encoder base64Encoder() {
        return Base64.getEncoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(Base64.Encoder encoder) {
        return requestTemplate -> {
            String credentials = shopId + ":" + secretKey;
            String encodedCredentials = encoder.encodeToString(credentials.getBytes());;
            requestTemplate.header("Authorization", "Basic " + encodedCredentials);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}